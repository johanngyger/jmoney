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

        Category transfer = createCategory(Category.Type.TRANSFER, "[TRANSFER]", root, cList);
        session.setTransferCategory(transfer);

        Category split = createCategory(Category.Type.SPLIT, "[SPLIT]", root, cList);
        session.setSplitCategory(split);

        createNormalCategory("Taxes", root, cList);
        createNormalCategory("Memberships", root, cList);
        createNormalCategory("Donations", root, cList);
        createNormalCategory("Fees", root, cList);
        createNormalCategory("Gifts", root, cList);

        Category income = createNormalCategory("Income", root, cList);
        createNormalCategory("Wages", income, cList);
        createNormalCategory("Sidelines", income, cList);

        Category children = createNormalCategory("Children", root, cList);
        createNormalCategory("Doctor", children, cList);
        createNormalCategory("Clothing", children, cList);
        createNormalCategory("Child care", children, cList);
        createNormalCategory("Toys", children, cList);

        Category housing = createNormalCategory("Living", root, cList);
        createNormalCategory("Additional costs", housing, cList);
        createNormalCategory("Rent/Mortgage", housing, cList);
        createNormalCategory("TV", housing, cList);

        Category communication = createNormalCategory("Communication", root, cList);
        createNormalCategory("Phone", communication, cList);
        createNormalCategory("Mobile", communication, cList);
        createNormalCategory("Internet", communication, cList);

        Category insurance = createNormalCategory("Insurance", root, cList);
        createNormalCategory("Health insurance", insurance, cList);
        createNormalCategory("Household insurance", insurance, cList);

        Category household = createNormalCategory("Household", root, cList);
        createNormalCategory("Food", household, cList);
        createNormalCategory("Restaurant", household, cList);
        createNormalCategory("Clothing", household, cList);

        Category transport = createNormalCategory("Traffic", root, cList);
        createNormalCategory("Car", transport, cList);
        createNormalCategory("Public transport", transport, cList);

        Category entertainment = createNormalCategory("Entertainment", root, cList);
        createNormalCategory("Books", entertainment, cList);
        createNormalCategory("Newspapers", entertainment, cList);
        createNormalCategory("Magazines", entertainment, cList);
        createNormalCategory("Music", entertainment, cList);
        createNormalCategory("Movies", entertainment, cList);
        createNormalCategory("Games", entertainment, cList);

        Category leisure = createNormalCategory("Leisure", root, cList);
        createNormalCategory("Outgoing", leisure, cList);
        createNormalCategory("Cinema", leisure, cList);
        createNormalCategory("Sports", leisure, cList);
        createNormalCategory("Concerts", leisure, cList);
        createNormalCategory("Trips", leisure, cList);
        createNormalCategory("Vacation", leisure, cList);

        Category healthCare = createNormalCategory("Health", root, cList);
        createNormalCategory("Doctor", healthCare, cList);
        createNormalCategory("Drugstore", healthCare, cList);
        createNormalCategory("Dentist", healthCare, cList);
        createNormalCategory("Hygiene", healthCare, cList);

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
