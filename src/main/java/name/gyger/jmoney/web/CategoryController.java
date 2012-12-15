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

package name.gyger.jmoney.web;

import name.gyger.jmoney.dto.CategoryDto;
import name.gyger.jmoney.dto.CategoryNodeDto;
import name.gyger.jmoney.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Collection;

@Controller
public class CategoryController {

    @Inject
    private CategoryService categoryService;

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    @ResponseBody
    public Collection<CategoryDto> getCategories() {
        return categoryService.getCategories();
    }

    @RequestMapping(value = "/split-category", method = RequestMethod.GET)
    @ResponseBody
    public CategoryDto getSplitCategory() {
        return categoryService.getSplitCategory();
    }

    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    @ResponseBody
    public long createCategory(@RequestBody CategoryNodeDto dto) {
        return categoryService.createCategory(dto);
    }

    @RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteCategory(@PathVariable long categoryId) {
        categoryService.deleteCategory(categoryId);
    }

    @RequestMapping(value = "/category-tree", method = RequestMethod.GET)
    @ResponseBody
    public CategoryNodeDto getCategoryTree() {
        return categoryService.getCategoryTree();
    }

    @RequestMapping(value = "/category-tree", method = RequestMethod.PUT)
    @ResponseBody
    public void saveCategoryTree(@RequestBody CategoryNodeDto dto) {
        categoryService.saveCategoryTree(dto);
    }

}
