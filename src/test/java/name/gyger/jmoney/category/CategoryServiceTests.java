package name.gyger.jmoney.category;

import name.gyger.jmoney.category.CategoryDto;
import name.gyger.jmoney.category.CategoryNodeDto;
import name.gyger.jmoney.category.CategoryService;
import name.gyger.jmoney.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CategoryServiceTests {

    @Autowired
    SessionService sessionService;

    @Autowired
    CategoryService categoryService;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp() {
        sessionService.initSession();
        em.flush();
        em.clear();
    }

    @Test
    public void testBasics() {
        List<CategoryDto> categories = categoryService.getCategories();
        int size = categories.size();
        assertThat(categories).isNotEmpty();

        CategoryNodeDto categoryTree = categoryService.getCategoryTree();
        assertThat(categoryTree).isNotNull();
        assertThat(categoryTree.getName()).isEqualTo("[ROOT]");
        assertThat(categoryTree.getChildren()).isNotEmpty();

        categoryTree.setName("[ROOT2]");
        categoryService.saveCategoryTree(categoryTree);
        categoryTree = categoryService.getCategoryTree();
        assertThat(categoryTree.getName()).isEqualTo("[ROOT2]");

        CategoryDto splitCategory = categoryService.getSplitCategory();
        assertThat(splitCategory.getName()).isEqualTo("[SPLITTBUCHUNG]");

        CategoryNodeDto newCat = new CategoryNodeDto();
        newCat.setName("NEW");
        newCat.setParentId(categoryTree.getId());
        long newCatId = categoryService.createCategory(newCat);
        em.flush();
        em.clear();
        assertThat(categoryService.getCategories()).hasSize(size + 1);

        categoryService.deleteCategory(newCatId);
        em.flush();
        em.clear();
        assertThat(categoryService.getCategories()).hasSize(size);
    }


}
