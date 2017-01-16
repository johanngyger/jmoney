package name.gyger.jmoney.category

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest")
class CategoryController(private val categoryService: CategoryService) {

    @GetMapping("/categories")
    fun getCategories(): List<Category> {
        val categories = categoryService.categories
        categories.forEach { c ->
            c.parent = null
            c.children = null
        }
        return categories
    }

    @GetMapping("/split-category")
    fun getSplitCategory(): Category {
        return categoryService.splitCategory
    }

    @GetMapping("/root-category")
    fun getRootCategory(): Category {
        val rootCategory = categoryService.rootCategory
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
        val rootCategory = categoryService.categoryTree
        rootCategory.children.remove(categoryService.splitCategory)
        rootCategory.children.remove(categoryService.transferCategory)
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
