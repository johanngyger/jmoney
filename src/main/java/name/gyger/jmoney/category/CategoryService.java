package name.gyger.jmoney.category;

import name.gyger.jmoney.session.Session;
import name.gyger.jmoney.session.SessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CategoryService {

    private final SessionService sessionService;

    @PersistenceContext
    private EntityManager em;

    public CategoryService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public List<Category> prefetchCategories() {
        Query q = em.createQuery("SELECT c FROM Category c LEFT JOIN FETCH c.children", Category.class);
        return (List<Category>) q.getResultList();
    }

    public Category getRootCategory() {
        Session s = sessionService.getSession();
        prefetchCategories();
        return s.getRootCategory();
    }

    public List<Category> getCategories() {
        Category rootCategory = getRootCategory();
        List<Category> categories = new ArrayList<>();
        if (rootCategory != null) {
            addChildCategories(categories, rootCategory, 0);
        }
        categories.forEach(c -> {
            em.detach(c);
            Category parent = c.getParent();
            if (parent != null) c.setParentId(parent.getId());
        });
        return categories;
    }

    private void addChildCategories(List<Category> categories, Category parentCategory, int level) {
        for (Category childCategory : parentCategory.getChildren()) {
            childCategory.setLevel(level);
            categories.add(childCategory);
            addChildCategories(categories, childCategory, level + 1);
        }
    }

    public Category getCategoryTree() {
        Category root = getRootCategory();
        resolveChildCats(root, 0);
        return root;
    }

    private void resolveChildCats(Category node, int level) {
        Category parent = node.getParent();
        node.setLevel(level);
        if (parent != null) node.setParentId(parent.getId());
        node.getChildren().forEach(c -> resolveChildCats(c, level + 1));
    }

    public void saveCategoryTree(Category category) {
        resolveParents(category);
        em.merge(category);
    }

    private void resolveParents(Category node) {
        Category parent = em.find(Category.class, node.getParentId());
        node.setParent(parent);
        node.getChildren().forEach(c -> resolveParents(c));
    }

    public long createCategory(Category category) {
        Category parent = em.find(Category.class, category.getParentId());
        category.setParent(parent);
        em.persist(category);
        return category.getId();
    }

    public void deleteCategory(long categoryId) {
        Query q = em.createNativeQuery("UPDATE ENTRY SET CATEGORY_ID = NULL WHERE CATEGORY_ID = :categoryId");
        q.setParameter("categoryId", categoryId);
        q.executeUpdate();

        Category category = em.find(Category.class, categoryId);
        em.remove(category);
    }

    public Category getSplitCategory() {
        return sessionService.getSession().getSplitCategory();
    }

    public Category getTransferCategory() {
        return sessionService.getSession().getTransferCategory();
    }

}
