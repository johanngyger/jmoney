package name.gyger.jmoney.category;

import name.gyger.jmoney.session.Session;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryFactory {

    @PersistenceContext
    private EntityManager em;

    public void createNormalCategories(Category root) {
        createNormalCategory("Taxes", root);
        createNormalCategory("Memberships", root);
        createNormalCategory("Donations", root);
        createNormalCategory("Fees", root);
        createNormalCategory("Gifts", root);

        Category income = createNormalCategory("Income", root);
        createNormalCategory("Wages", income);
        createNormalCategory("Sidelines", income);

        Category children = createNormalCategory("Children", root);
        createNormalCategory("Doctor", children);
        createNormalCategory("Clothing", children);
        createNormalCategory("Child care", children);
        createNormalCategory("Toys", children);

        Category housing = createNormalCategory("Living", root);
        createNormalCategory("Additional costs", housing);
        createNormalCategory("Rent/Mortgage", housing);
        createNormalCategory("TV", housing);

        Category communication = createNormalCategory("Communication", root);
        createNormalCategory("Phone", communication);
        createNormalCategory("Mobile", communication);
        createNormalCategory("Internet", communication);

        Category insurance = createNormalCategory("Insurance", root);
        createNormalCategory("Health insurance", insurance);
        createNormalCategory("Household insurance", insurance);

        Category household = createNormalCategory("Household", root);
        createNormalCategory("Food", household);
        createNormalCategory("Restaurant", household);
        createNormalCategory("Clothing", household);

        Category transport = createNormalCategory("Traffic", root);
        createNormalCategory("Car", transport);
        createNormalCategory("Public transport", transport);

        Category entertainment = createNormalCategory("Entertainment", root);
        createNormalCategory("Books", entertainment);
        createNormalCategory("Newspapers", entertainment);
        createNormalCategory("Magazines", entertainment);
        createNormalCategory("Music", entertainment);
        createNormalCategory("Movies", entertainment);
        createNormalCategory("Games", entertainment);

        Category leisure = createNormalCategory("Leisure", root);
        createNormalCategory("Outgoing", leisure);
        createNormalCategory("Cinema", leisure);
        createNormalCategory("Sports", leisure);
        createNormalCategory("Concerts", leisure);
        createNormalCategory("Trips", leisure);
        createNormalCategory("Vacation", leisure);

        Category healthCare = createNormalCategory("Health", root);
        createNormalCategory("Doctor", healthCare);
        createNormalCategory("Drugstore", healthCare);
        createNormalCategory("Dentist", healthCare);
        createNormalCategory("Hygiene", healthCare);
    }

    public Category createRootCategory() {
        return createCategory(Category.Type.ROOT, "[ROOT]", null);
    }

    public Category createTransferCategory(Category root) {
        return createCategory(Category.Type.TRANSFER, "[TRANSFER]", root);
    }

    public Category createSplitCategory(Category root) {
        return createCategory(Category.Type.SPLIT, "[SPLIT]", root);
    }

    private Category createNormalCategory(String name, Category parent) {
        return createCategory(Category.Type.NORMAL, name, parent);
    }

    private Category createCategory(Category.Type type, String name, Category parent) {
        Category c = new Category(type, name);
        c.setParent(parent);
        em.persist(c);
        return c;
    }

}
