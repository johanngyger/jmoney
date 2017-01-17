package name.gyger.jmoney.category

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest")
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping("/categories")
    fun getCategories(): List<Category> {
        val categories = categoryService.getCategories()
        categories.forEach { c ->
            c.parent = null
            c.children = null
        }
        return categories
    }

    @GetMapping("/split-category")
    fun getSplitCategory(): Category {
        return categoryService.getSplitCategory()
    }

    @GetMapping("/root-category")
    fun getRootCategory(): Category {
        val rootCategory = categoryService.getRootCategory()
        rootCategory.children = null
        return rootCategory
    }

    @PostMapping("/categories")
    fun createCategory(@RequestBody category: Category): Long {
        return categoryService.createCategory(category)
    }

    @DeleteMapping("/categories/{categoryId}")
    fun deleteCategory(@PathVariable categoryId: Long) {
        categoryService.deleteCategory(categoryId)
    }

    @GetMapping("/category-tree")
    fun getCategoryMapping(): Category {
        val rootCategory = categoryService.getCategoryTree()
        rootCategory.children.remove(categoryService.getSplitCategory())
        rootCategory.children.remove(categoryService.getTransferCategory())
        cleanupCategories(rootCategory)
        return rootCategory
    }

    private fun cleanupCategories(cat: Category) {
        cat.parent = null
        cat.children.forEach { c -> cleanupCategories(c) }
    }

    @PutMapping("/category-tree")
    fun saveCategoryTree(@RequestBody rootCat: Category) {
        categoryService.saveCategoryTree(rootCat)
    }

}
