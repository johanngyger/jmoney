package name.gyger.jmoney.category;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RequestMapping(path = "/categories", method = RequestMethod.GET)
    public List<Category> getCategories() {
        List<Category> categories = categoryService.getCategories();
        categories.forEach(c -> {
            c.setParent(null);
            c.setChildren(null);
        });
        return categories;
    }

    @RequestMapping(path = "/split-category", method = RequestMethod.GET)
    public Category getSplitCategory() {
        return categoryService.getSplitCategory();
    }

    @RequestMapping(path = "/root-category", method = RequestMethod.GET)
    public Category getRootCategory() {
        Category rootCategory = categoryService.getRootCategory();
        rootCategory.setChildren(null);
        return rootCategory;
    }

    @RequestMapping(path = "/categories", method = RequestMethod.POST)
    public long createCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @RequestMapping(path = "/categories/{categoryId}", method = RequestMethod.DELETE)
    public void deleteCategory(@PathVariable long categoryId) {
        categoryService.deleteCategory(categoryId);
    }

    @RequestMapping(path = "/category-tree", method = RequestMethod.GET)
    public Category getCategoryTree() {
        Category rootCategory = categoryService.getCategoryTree();
        rootCategory.getChildren().remove(categoryService.getSplitCategory());
        rootCategory.getChildren().remove(categoryService.getTransferCategory());
        cleanupCategories(rootCategory);
        return rootCategory;
    }

    private void cleanupCategories(Category cat) {
        cat.setParent(null);
        cat.getChildren().forEach(c -> cleanupCategories(c));
    }

    @RequestMapping(path = "/category-tree", method = RequestMethod.PUT)
    public void saveCategoryTree(@RequestBody Category rootCat) {
        categoryService.saveCategoryTree(rootCat);
    }

}
