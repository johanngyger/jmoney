package name.gyger.jmoney.category

import name.gyger.jmoney.session.SessionService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
open class CategoryServiceTests {

    @Autowired
    lateinit var sessionService: SessionService

    @Autowired
    lateinit var categoryService: CategoryService

    @PersistenceContext
    lateinit private var em: EntityManager

    @Before
    fun setUp() {
        sessionService.initSession()
        em.flush()
        em.clear()
    }

    @Test
    fun testBasics() {
        val categories = categoryService.getCategories()
        val size = categories.size
        assertThat(categories).isNotEmpty()

        val newCat = Category()
        newCat.name = "NEW"
        newCat.parentId = categoryService.getRootCategory().id
        val newCatId = categoryService.createCategory(newCat)
        em.flush()
        em.clear()
        assertThat(categoryService.getCategories()).hasSize(size + 1)

        categoryService.deleteCategory(newCatId)
        em.flush()
        em.clear()
        assertThat(categoryService.getCategories()).hasSize(size)
    }

    @Test
    fun testGetSplitCategory() {
        val splitCategory = categoryService.getSplitCategory()
        assertThat(splitCategory.name).isEqualTo("[SPLIT]")
    }

    @Test
    fun testGetTransferCategory() {
        val splitCategory = categoryService.getTransferCategory()
        assertThat(splitCategory.name).isEqualTo("[TRANSFER]")
    }

    @Test
    fun testCategoryTree() {
        var categoryTree = categoryService.getCategoryTree()
        assertThat(categoryTree).isNotNull()
        assertThat(categoryTree.name).isEqualTo("[ROOT]")
        assertThat(categoryTree.children).isNotEmpty()

        categoryTree.name = "[ROOT2]"
        categoryService.saveCategoryTree(categoryTree)
        categoryTree = categoryService.getCategoryTree()
        assertThat(categoryTree.name).isEqualTo("[ROOT2]")
    }

}
