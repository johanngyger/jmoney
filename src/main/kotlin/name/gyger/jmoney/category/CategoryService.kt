package name.gyger.jmoney.category

import name.gyger.jmoney.account.EntryRepository
import name.gyger.jmoney.session.SessionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
open class CategoryService(private val sessionRepository: SessionRepository,
                           private val categoryRepository: CategoryRepository,
                           private val entryRepository: EntryRepository) {

    fun getRootCategory(): Category {
        categoryRepository.findAll()  // prefetch
        return sessionRepository.getSession().rootCategory
    }

    fun getCategories(): List<Category> {
        val categories = ArrayList<Category>()
        addChildCategories(categories, getRootCategory(), 0)
        categories.forEach { c ->
            c.parentId = c.parent?.id ?: 0
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
        categoryRepository.save(category)
    }

    private fun resolveParents(node: Category) {
        node.parent = categoryRepository.findOne(node.parentId)
        node.children.forEach { c -> resolveParents(c) }
    }

    fun createCategory(category: Category): Long {
        category.parent = categoryRepository.findOne(category.parentId)
        categoryRepository.save(category)
        return category.id
    }

    fun deleteCategory(categoryId: Long) {
        entryRepository.deleteCategoryFromEntry(categoryId)
        categoryRepository.delete(categoryId)
    }

    fun getSplitCategory(): Category {
        return sessionRepository.getSession().splitCategory
    }

    fun getTransferCategory(): Category {
        return sessionRepository.getSession().transferCategory
    }

}
