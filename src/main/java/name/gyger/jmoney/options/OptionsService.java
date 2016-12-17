/*
 * Copyright 2012 Johann Gyger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package name.gyger.jmoney.options;

import name.gyger.jmoney.account.Account;
import name.gyger.jmoney.category.Category;
import name.gyger.jmoney.account.Entry;
import name.gyger.jmoney.session.Session;
import name.gyger.jmoney.session.SessionService;
import net.sf.jmoney.XMLReader;
import net.sf.jmoney.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class OptionsService {

    private static final Logger log = LoggerFactory.getLogger(OptionsService.class);

    private static final Entry.Status[] entryStates = {null, Entry.Status.RECONCILING, Entry.Status.CLEARED};

    @PersistenceContext
    private EntityManager em;

    private Session session;

    private net.sf.jmoney.model.Session oldSession;

    private Map<net.sf.jmoney.model.Category, Category> oldToNewCategoryMap = new HashMap<net.sf.jmoney.model.Category, Category>();

    private Map<Entry, net.sf.jmoney.model.Category> entryToOldCategoryMap = new HashMap<Entry, net.sf.jmoney.model.Category>();

    private Map<net.sf.jmoney.model.DoubleEntry, Entry> oldToNewDoubleEntryMap = new HashMap<net.sf.jmoney.model.DoubleEntry, Entry>();

    private final SessionService sessionService;

    public OptionsService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void importFile(InputStream in) {
        sessionService.removeOldSession();

        oldSession = XMLReader.readSessionFromInputStream(in);

        session = new Session();
        em.persist(session);

        mapCategoryNode(oldSession.getCategories().getRootNode(), null);
        mapRootCategoryToSession();
        mapCategoryToEntry();
        mapDoubleEntries();
    }

    private void mapCategoryNode(net.sf.jmoney.model.CategoryNode node, Category parent) {
        net.sf.jmoney.model.Category oldCat = node.getCategory();
        if (oldCat instanceof net.sf.jmoney.model.Account) {
            mapAccount((net.sf.jmoney.model.Account) oldCat, parent);
        } else {
            mapCategory(node, parent);
        }
    }

    private void mapAccount(net.sf.jmoney.model.Account oldAcc, Category parent) {
        Account acc = new Account();
        acc.setAbbreviation(oldAcc.getAbbrevation());
        acc.setAccountNumber(oldAcc.getAccountNumber());
        acc.setBank(oldAcc.getBank());
        acc.setName(oldAcc.getCategoryName());
        acc.setComment(oldAcc.getComment());
        acc.setCurrencyCode(oldAcc.getCurrencyCode());
        acc.setMinBalance(oldAcc.getMinBalance());
        acc.setStartBalance(oldAcc.getStartBalance());
        acc.setSession(session);
        acc.setParent(parent);

        em.persist(acc);

        oldToNewCategoryMap.put(oldAcc, acc);

        mapEntries(oldAcc, acc);
    }

    private void mapCategory(net.sf.jmoney.model.CategoryNode node,
                             Category parent) {
        net.sf.jmoney.model.Category oldCat = node.getCategory();
        Category cat = createCategory(parent, oldCat);

        for (int i = 0; i < node.getChildCount(); i++) {
            net.sf.jmoney.model.CategoryNode childNode = (CategoryNode) node.getChildAt(i);
            mapCategoryNode(childNode, cat);
        }
    }

    private Category createCategory(Category parent,
                                    net.sf.jmoney.model.Category oldCat) {
        Category cat = new Category();
        if (oldCat instanceof SplitCategory) {
            cat.setType(Category.Type.SPLIT);
            session.setSplitCategory(cat);
        } else if (oldCat instanceof TransferCategory) {
            cat.setType(Category.Type.TRANSFER);
            session.setTransferCategory(cat);
        } else if (oldCat instanceof RootCategory) {
            cat.setType(Category.Type.ROOT);
            session.setTransferCategory(cat);
        }
        cat.setName(oldCat.getCategoryName());
        cat.setParent(parent);

        em.persist(cat);

        oldToNewCategoryMap.put(oldCat, cat);
        if (oldCat instanceof SplitCategory) {
            // Workaround for redundant split category.
            net.sf.jmoney.model.Category oldCat2 = oldSession.getCategories().getSplitNode().getCategory();
            oldToNewCategoryMap.put(oldCat2, cat);
        }
        return cat;
    }

    @SuppressWarnings("unchecked")
    private void mapEntries(net.sf.jmoney.model.Account oldAcc, Account acc) {
        for (Object o : oldAcc.getEntries()) {
            net.sf.jmoney.model.Entry oldEntry = (net.sf.jmoney.model.Entry) o;

            if (oldEntry instanceof SplittedEntry) {
                mapSplitEntry(acc, oldEntry);
            } else {
                mapEntryOrDoubleEntry(acc, null, oldEntry);
            }

        }
    }

    @SuppressWarnings("unchecked")
    private void mapSplitEntry(Account acc, net.sf.jmoney.model.Entry oldEntry) {
        net.sf.jmoney.model.SplittedEntry oldSe = (net.sf.jmoney.model.SplittedEntry) oldEntry;
        Entry splitEntry = new Entry();

        mapEntry(splitEntry, acc, null, oldSe);

        for (Object o : oldSe.getEntries()) {
            net.sf.jmoney.model.Entry oldSubEntry = (net.sf.jmoney.model.Entry) o;
            oldSubEntry.setDate(oldSe.getDate());  // fix for wrong date from old model
            mapEntryOrDoubleEntry(null, splitEntry, oldSubEntry);
        }
    }

    private void mapEntryOrDoubleEntry(Account acc, Entry splitEntry, net.sf.jmoney.model.Entry oldEntry) {
        if (oldEntry instanceof net.sf.jmoney.model.DoubleEntry) {
            mapDoubleEntry(new Entry(), acc, splitEntry, (net.sf.jmoney.model.DoubleEntry) oldEntry);
        } else {
            mapEntry(new Entry(), acc, splitEntry, oldEntry);
        }
    }

    private void mapDoubleEntry(Entry doubleEntry, Account acc, Entry splitEntry, net.sf.jmoney.model.DoubleEntry oldDe) {
        mapEntry(doubleEntry, acc, splitEntry, oldDe);
        oldToNewDoubleEntryMap.put(oldDe, doubleEntry);
    }

    private void mapEntry(Entry e, Account acc, Entry splitEntry, net.sf.jmoney.model.Entry oldEntry) {
        e.setAccount(acc);
        e.setSplitEntry(splitEntry);
        e.setAmount(oldEntry.getAmount());
        e.setCreation(oldEntry.getCreation());
        e.setDate(oldEntry.getDate());
        e.setDescription(oldEntry.getDescription());
        e.setMemo(oldEntry.getMemo());
        e.setStatus(entryStates[oldEntry.getStatus()]);
        e.setValuta(oldEntry.getValuta());

        // Category might not exist yet, so this is done in a second pass.
        net.sf.jmoney.model.Category oldCat = oldEntry.getCategory();
        if (oldCat != null) {
            entryToOldCategoryMap.put(e, oldCat);
        }

        em.persist(e);
    }

    private void mapRootCategoryToSession() {
        net.sf.jmoney.model.Category oldRootCat = oldSession.getCategories().getRootNode().getCategory();
        Category rootCat = oldToNewCategoryMap.get(oldRootCat);
        session.setRootCategory(rootCat);
    }

    private void mapCategoryToEntry() {
        for (Map.Entry<Entry, net.sf.jmoney.model.Category> mapEntry : entryToOldCategoryMap.entrySet()) {
            Entry e = mapEntry.getKey();
            net.sf.jmoney.model.Category oldCat = mapEntry.getValue();
            Category c = oldToNewCategoryMap.get(mapEntry.getValue());
            if (c == null) {
                Category root = session.getRootCategory();
                createCategory(root, oldCat);
            }
            e.setCategory(c);
        }
    }

    private void mapDoubleEntries() {
        for (Map.Entry<net.sf.jmoney.model.DoubleEntry, Entry> mapEntry : oldToNewDoubleEntryMap.entrySet()) {
            net.sf.jmoney.model.DoubleEntry oldDe = mapEntry.getKey();
            Entry de = mapEntry.getValue();
            Entry otherDe = oldToNewDoubleEntryMap.get(oldDe.getOther());
            if (otherDe == null) {
                log.warn("Dangling double entry: " + oldDe.getDescription() + ", " + oldDe.getFullCategoryName() + ", " + oldDe.getDate());
            }
            de.setOther(otherDe);
        }
    }

}
