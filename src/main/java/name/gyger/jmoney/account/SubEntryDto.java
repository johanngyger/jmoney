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

package name.gyger.jmoney.account;

import name.gyger.jmoney.category.Category;

public class SubEntryDto {

    private String description;
    private long categoryId;
    private long amount;

    public SubEntryDto() {
    }

    public SubEntryDto(Entry e) {
        description = e.getDescription();
        amount = e.getAmount();
        Category c = e.getCategory();
        if (c != null) {
            categoryId = c.getId();
        }
    }

    public void mapToModel(Entry subEntry) {
        subEntry.setDescription(description);
        subEntry.setAmount(amount);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

}
