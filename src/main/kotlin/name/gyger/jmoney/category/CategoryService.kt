package name.gyger.jmoney.category

import name.gyger.jmoney.session.Session
import name.gyger.jmoney.session.SessionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Query
import java.util.ArrayList

@Service
@Transactional
class CategoryService(private val sessionService: SessionService) {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun prefetchCategories(): List<Category> {
        return em.createQuery("SELECT c FROM Category c LEFT JOIN FETCH c.children", Category::class.java)
                .resultList
    }

    fun getRootCategory(): Category {
        prefetchCategories()
        return sessionService.getSession().rootCategory
    }

    fun getCategories(): List<Category> {
        val categories = ArrayList<Category>()
        addChildCategories(categories, getRootCategory(), 0)
        categories.forEach { c ->
            em.detach(c)
            c.parentId = c.parent!!.id
        }
        return categories
    }

    private fun addChildCategories(categories: MutableList<Category>, parentCategory: Category, level: Int) {
        parentCategory.children.forEach {
            it.level = level
            categories.add(it)
            addChildCategories(categories, it, level + 1)
        }
    }

    fun getCategoryTree(): Category {
        val root = getRootCategory()
        resolveChildCats(root, 0)
        return root
    }

    private fun resolveChildCats(node: Category, level: Int) {
        val parent = node.parent
        node.level = level
        if (parent != null) node.parentId = parent.id
        node.children.forEach { c -> resolveChildCats(c, level + 1) }
    }

    fun saveCategoryTree(category: Category) {
        resolveParents(category)
        em.merge(category)
    }

    private fun resolveParents(node: Category) {
        node.parent = em.find(Category::class.java, node.parentId)
        node.children.forEach { c -> resolveParents(c) }
    }

    fun createCategory(category: Category): Long {
        category.parent = em.find(Category::class.java, category.parentId)
        em.persist(category)
        return category.id
    }

    fun deleteCategory(categoryId: Long) {
        val q = em.createNativeQuery("UPDATE ENTRY SET CATEGORY_ID = NULL WHERE CATEGORY_ID = :categoryId")
        q.setParameter("categoryId", categoryId)
        q.executeUpdate()

        em.remove(em.find(Category::class.java, categoryId))
    }

    fun getSplitCategory(): Category {
        return sessionService.getSession().splitCategory
    }

    fun getTransferCategory(): Category {
        return sessionService.getSession().transferCategory
    }

}
