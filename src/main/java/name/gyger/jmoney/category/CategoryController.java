/*
 * Copyright 2012 Johann Gyger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        Category rootCategory = categoryService.getRootCategory();
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
