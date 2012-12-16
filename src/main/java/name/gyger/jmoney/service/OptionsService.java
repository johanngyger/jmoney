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

package name.gyger.jmoney.service;

import name.gyger.jmoney.model.Account;
import name.gyger.jmoney.model.Category;
import name.gyger.jmoney.model.*;
import name.gyger.jmoney.model.Entry;
import name.gyger.jmoney.model.Session;
import net.sf.jmoney.XMLReader;
import net.sf.jmoney.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OptionsService {

    private static final Logger log = LoggerFactory.getLogger(OptionsService.class);

    @PersistenceContext
    private EntityManager em;

    private Session session;

    private net.sf.jmoney.model.Session oldSession;

    private Map<net.sf.jmoney.model.Category, Category> oldToNewCategoryMap = new HashMap<>();

    private Map<Entry, net.sf.jmoney.model.Category> entryToOldCategoryMap = new HashMap<>();

    private Map<net.sf.jmoney.model.DoubleEntry, Entry> oldToNewDoubleEntryMap = new HashMap<>();

    @Inject
    private SessionService sessionService;

    public void init() {
        removeOldSession();
        Session session = new Session();
        initCategories(session);
        em.persist(session);
    }

    public Category initCategories(Session session) {
        List<Category> cList = new ArrayList<>();

        Category root = createCategory(CategoryType.ROOT, "[ROOT]", null, cList);
        session.setRootCategory(root);

        Category transfer = createCategory(CategoryType.TRANSFER, "[UMBUCHUNG]", root, cList);
        session.setTransferCategory(transfer);

        Category split = createCategory(CategoryType.SPLIT, "[SPLITTBUCHUNG]", root, cList);
        session.setSplitCategory(split);

        createNormalCategory("Steuern", root, cList);
        createNormalCategory("Mitgliedschaften", root, cList);
        createNormalCategory("Spenden", root, cList);
        createNormalCategory("Gebühren", root, cList);
        createNormalCategory("Geschenke", root, cList);

        Category income = createNormalCategory("Einkünfte", root, cList);
        createNormalCategory("Lohn", income, cList);
        createNormalCategory("Nebenerwerb", income, cList);
        createNormalCategory("Wertschriftenerträge", income, cList);

        Category children = createNormalCategory("Kinder", root, cList);
        createNormalCategory("Arzt", children, cList);
        createNormalCategory("Kleidung", children, cList);
        createNormalCategory("Hüten", children, cList);
        createNormalCategory("Spielsachen", children, cList);

        Category housing = createNormalCategory("Wohnen", root, cList);
        createNormalCategory("Nebenkosten/Unterhalt", housing, cList);
        createNormalCategory("Miete/Hypozins", housing, cList);
        createNormalCategory("TV", housing, cList);

        Category communication = createNormalCategory("Kommunikation", root, cList);
        createNormalCategory("Telefon", communication, cList);
        createNormalCategory("Mobile", communication, cList);
        createNormalCategory("Internet", communication, cList);

        Category insurance = createNormalCategory("Versicherungen", root, cList);
        createNormalCategory("Krankenkasse", insurance, cList);
        createNormalCategory("Haushalt/Haftpflicht", insurance, cList);

        Category household = createNormalCategory("Haushalt", root, cList);
        createNormalCategory("Lebensmittel", household, cList);
        createNormalCategory("Ausser-Haus-Verpflegung", household, cList);
        createNormalCategory("Kleidung", household, cList);

        Category transport = createNormalCategory("Verkehr", root, cList);
        createNormalCategory("Auto", transport, cList);
        createNormalCategory("ÖV", transport, cList);

        Category entertainment = createNormalCategory("Unterhaltung", root, cList);
        createNormalCategory("Bücher", entertainment, cList);
        createNormalCategory("Zeitungen", entertainment, cList);
        createNormalCategory("Zeitschriften", entertainment, cList);
        createNormalCategory("Musik", entertainment, cList);
        createNormalCategory("Filme", entertainment, cList);
        createNormalCategory("Spiele", entertainment, cList);

        Category leisure = createNormalCategory("Freizeit", root, cList);
        createNormalCategory("Ausgang", leisure, cList);
        createNormalCategory("Kino", leisure, cList);
        createNormalCategory("Sportanlässe", leisure, cList);
        createNormalCategory("Konzerte", leisure, cList);
        createNormalCategory("Ausflüge", leisure, cList);
        createNormalCategory("Bücher", leisure, cList);
        createNormalCategory("Ferien", leisure, cList);

        Category healthCare = createNormalCategory("Gesundheit", root, cList);
        createNormalCategory("Arzt", healthCare, cList);
        createNormalCategory("Apotheke", healthCare, cList);
        createNormalCategory("Zahnarzt", healthCare, cList);
        createNormalCategory("Körperpflege", healthCare, cList);

        for (Category c : cList) {
            em.persist(c);
        }

        return root;
    }

    private Category createCategory(CategoryType type, String name, Category parent, List<Category> cList) {
        Category c = new Category(type, name);
        c.setParent(parent);
        cList.add(c);
        return c;
    }

    private Category createNormalCategory(String name, Category parent, List<Category> cList) {
        Category c = new Category(CategoryType.NORMAL, name);
        c.setParent(parent);
        cList.add(c);
        return c;
    }

    private void removeOldSession() {
        if (sessionService.isSessionAvailable()) {
            Session oldSession = sessionService.getSession();
            em.remove(oldSession);
        }
    }

    public void importFile(InputStream in) {
        removeOldSession();

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
            cat.setType(CategoryType.SPLIT);
            session.setSplitCategory(cat);
        } else if (oldCat instanceof TransferCategory) {
            cat.setType(CategoryType.TRANSFER);
            session.setTransferCategory(cat);
        } else if (oldCat instanceof RootCategory) {
            cat.setType(CategoryType.ROOT);
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
        e.setStatus(oldEntry.getStatus());
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
