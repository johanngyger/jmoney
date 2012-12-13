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

package name.gyger.jmoney.dto;

import name.gyger.jmoney.model.Category;
import name.gyger.jmoney.model.CategoryType;

import java.util.ArrayList;
import java.util.List;

public class CategoryNodeDto {

    private String name;
    private long id;
    private long parentId;
    private List<CategoryNodeDto> children = new ArrayList<CategoryNodeDto>();

    public CategoryNodeDto() {
    }

    public CategoryNodeDto(Category category) {
        name = category.getName();
        id = category.getId();
        Category parent = category.getParent();
        if (parent != null) {
            parentId = category.getParent().getId();
        }

        for (Category child : category.getChildren()) {
            if (child.getType() == CategoryType.NORMAL) {
                CategoryNodeDto childDto = new CategoryNodeDto(child);
                children.add(childDto);
            }
        }
    }

    public void mapToModel(Category c) {
        c.setName(getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<CategoryNodeDto> getChildren() {
        return children;
    }

    public void setChildren(List<CategoryNodeDto> children) {
        this.children = children;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

}