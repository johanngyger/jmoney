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

package name.gyger.jmoney.session;

import name.gyger.jmoney.account.Account;
import name.gyger.jmoney.category.Category;

import javax.persistence.*;
import java.util.List;

@Entity
public class Session {

    @Id
    @GeneratedValue
    private long id;

    @OneToMany(mappedBy = "session", orphanRemoval = true)
    private List<Account> accounts;

    @OneToOne(orphanRemoval = true)
    private Category rootCategory;

    @OneToOne
    private Category transferCategory;

    @OneToOne
    private Category splitCategory;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public Category getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(Category rootCategory) {
        this.rootCategory = rootCategory;
    }

    public Category getTransferCategory() {
        return transferCategory;
    }

    public void setTransferCategory(Category transferCategory) {
        this.transferCategory = transferCategory;
    }

    public Category getSplitCategory() {
        return splitCategory;
    }

    public void setSplitCategory(Category splitCategory) {
        this.splitCategory = splitCategory;
    }

}
