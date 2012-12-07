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

import name.gyger.jmoney.model.*;
import net.sf.jmoney.XMLReader;
import net.sf.jmoney.model.CategoryNode;
import net.sf.jmoney.model.SplitCategory;
import net.sf.jmoney.model.SplittedEntry;
import net.sf.jmoney.model.TransferCategory;
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

    private Map<net.sf.jmoney.model.Category, Category> oldToNewCategoryMap = new HashMap<net.sf.jmoney.model.Category, Category>();

    private Map<Entry, net.sf.jmoney.model.Category> entryToOldCategoryMap = new HashMap<Entry, net.sf.jmoney.model.Category>();

    private Map<net.sf.jmoney.model.DoubleEntry, DoubleEntry> oldToNewDoubleEntryMap = new HashMap<net.sf.jmoney.model.DoubleEntry, DoubleEntry>();

    @Inject
    private SessionService sessionService;

    public void init() {
        removeOldSession();
        Session session = new Session();
        Category root = initCategories();
        session.setRootCategory(root);
        em.persist(session);
    }

    public Category initCategories() {
        List<Category> cList = new ArrayList<Category>();

        Category root = createCategory("Root", null, cList);

        Category income = createCategory("Einnahmen", root, cList);
        createCategory("Lohn", income, cList);
        createCategory("Nebenerwerb", income, cList);
        createCategory("Wertschriftenerträge", income, cList);

        Category expenses = createCategory("Ausgaben", root, cList);
        createCategory("Steuern", expenses, cList);
        createCategory("Mitgliedschaften", expenses, cList);
        createCategory("Spenden", expenses, cList);
        createCategory("Gebühren", expenses, cList);
        createCategory("Geschenke", expenses, cList);

        Category children = createCategory("Kinder", expenses, cList);
        createCategory("Arzt", children, cList);
        createCategory("Kleidung", children, cList);
        createCategory("Hüten", children, cList);
        createCategory("Spielsachen", children, cList);

        Category housing = createCategory("Wohnen", expenses, cList);
        createCategory("Nebenkosten/Unterhalt", housing, cList);
        createCategory("Miete/Hypozins", housing, cList);
        createCategory("TV", housing, cList);

        Category communication = createCategory("Kommunikation", expenses, cList);
        createCategory("Telefon", communication, cList);
        createCategory("Mobile", communication, cList);
        createCategory("Internet", communication, cList);

        Category insurance = createCategory("Versicherungen", expenses, cList);
        createCategory("Krankenkasse", insurance, cList);
        createCategory("Haushalt/Haftpflicht", insurance, cList);

        Category household = createCategory("Haushalt", expenses, cList);
        createCategory("Lebensmittel", household, cList);
        createCategory("Ausser-Haus-Verpflegung", household, cList);
        createCategory("Kleidung", household, cList);

        Category transport = createCategory("Verkehr", expenses, cList);
        createCategory("Auto", transport, cList);
        createCategory("ÖV", transport, cList);

        Category entertainment = createCategory("Unterhaltung", expenses, cList);
        createCategory("Bücher", entertainment, cList);
        createCategory("Zeitungen", entertainment, cList);
        createCategory("Zeitschriften", entertainment, cList);
        createCategory("Musik", entertainment, cList);
        createCategory("Filme", entertainment, cList);
        createCategory("Spiele", entertainment, cList);

        Category leisure = createCategory("Freizeit", expenses, cList);
        createCategory("Ausgang", leisure, cList);
        createCategory("Kino", leisure, cList);
        createCategory("Sportanlässe", leisure, cList);
        createCategory("Konzerte", leisure, cList);
        createCategory("Ausflüge", leisure, cList);
        createCategory("Bücher", leisure, cList);
        createCategory("Ferien", leisure, cList);

        Category healthCare = createCategory("Gesundheit", expenses, cList);
        createCategory("Arzt", healthCare, cList);
        createCategory("Apotheke", healthCare, cList);
        createCategory("Zahnarzt", healthCare, cList);
        createCategory("Körperpflege", healthCare, cList);

        for (Category c : cList) {
            em.persist(c);
        }

        return root;
    }

    private Category createCategory(String name, Category parent, List<Category> cList) {
        Category c = new Category(name);
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
        Category cat;
        if (oldCat instanceof SplitCategory) {
            SpecialCategory c = new SpecialCategory();
            c.setSplitCategory(true);
            cat = c;
        } else if (oldCat instanceof TransferCategory) {
            SpecialCategory c = new SpecialCategory();
            c.setTransferCategory(true);
            cat = c;
        } else {
            cat = new Category();
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
        SplitEntry se = new SplitEntry();

        mapEntry(se, acc, null, oldSe);

        for (Object o : oldSe.getEntries()) {
            net.sf.jmoney.model.Entry oldSubEntry = (net.sf.jmoney.model.Entry) o;
            mapEntryOrDoubleEntry(null, se, oldSubEntry);
        }
    }

    private void mapEntryOrDoubleEntry(Account acc, SplitEntry se, net.sf.jmoney.model.Entry oldEntry) {
        if (oldEntry instanceof net.sf.jmoney.model.DoubleEntry) {
            mapDoubleEntry(new DoubleEntry(), acc, se, (net.sf.jmoney.model.DoubleEntry) oldEntry);
        } else {
            mapEntry(new Entry(), acc, se, oldEntry);
        }
    }

    private void mapDoubleEntry(DoubleEntry de, Account acc, SplitEntry se, net.sf.jmoney.model.DoubleEntry oldDe) {
        mapEntry(de, acc, se, oldDe);
        oldToNewDoubleEntryMap.put(oldDe, de);
    }

    private void mapEntry(Entry e, Account acc, SplitEntry se, net.sf.jmoney.model.Entry oldEntry) {
        e.setAccount(acc);
        e.setSplitEntry(se);
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
        for (Map.Entry<net.sf.jmoney.model.DoubleEntry, DoubleEntry> mapEntry : oldToNewDoubleEntryMap.entrySet()) {
            net.sf.jmoney.model.DoubleEntry oldDe = mapEntry.getKey();
            DoubleEntry de = mapEntry.getValue();
            DoubleEntry otherDe = oldToNewDoubleEntryMap.get(oldDe.getOther());
            if (otherDe == null) {
                log.warn("Dangling double entry: " + oldDe.getDescription() + ", " + oldDe.getFullCategoryName() + ", " + oldDe.getDate());
            }
            de.setOther(otherDe);
        }
    }

}
