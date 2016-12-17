package name.gyger.jmoney.category;

import name.gyger.jmoney.session.Session;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryInitializer {

    @PersistenceContext
    private EntityManager em;

    public Category initCategories(Session session) {
        List<Category> cList = new ArrayList<Category>();

        Category root = createCategory(Category.Type.ROOT, "[ROOT]", null, cList);
        session.setRootCategory(root);

        Category transfer = createCategory(Category.Type.TRANSFER, "[UMBUCHUNG]", root, cList);
        session.setTransferCategory(transfer);

        Category split = createCategory(Category.Type.SPLIT, "[SPLITTBUCHUNG]", root, cList);
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

    private Category createCategory(Category.Type type, String name, Category parent, List<Category> cList) {
        Category c = new Category(type, name);
        c.setParent(parent);
        cList.add(c);
        return c;
    }

    private Category createNormalCategory(String name, Category parent, List<Category> cList) {
        Category c = new Category(Category.Type.NORMAL, name);
        c.setParent(parent);
        cList.add(c);
        return c;
    }

}
