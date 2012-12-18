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

import name.gyger.jmoney.model.Account;
import name.gyger.jmoney.model.Category;
import name.gyger.jmoney.model.Entry;

import java.util.Date;

public class EntryDto {

    private long id;
    private Date date;
    private Date valuta;
    private String description;
    private long amount;
    private Entry.Status status;
    private String memo;
    private long categoryId;
    private String categoryName;
    private long balance;
    private long accountId;
    private String accountName;

    public EntryDto() {
    }

    public EntryDto(Entry e) {
        id = e.getId();
        date = e.getDate();
        valuta = e.getValuta();
        description = e.getDescription();
        amount = e.getAmount();
        status = e.getStatus();
        memo = e.getMemo();
        Category c = e.getCategory();
        if (c != null) {
            categoryId = c.getId();
            categoryName = c.getName();
        }
        Account a = e.getAccount();
        accountId = a.getId();
        accountName = a.getName();
    }

    public void mapToModel(Entry entry) {
        entry.setAmount(getAmount());
        entry.setDate(getDate());
        entry.setDescription(getDescription());
        entry.setMemo(getMemo());
        entry.setStatus(getStatus());
        entry.setValuta(getValuta());

        Entry other = entry.getOther();
        if (other != null) {
            other.setAmount(-amount);
            other.setDescription(description);
            other.setMemo(memo);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getValuta() {
        return valuta;
    }

    public void setValuta(Date valuta) {
        this.valuta = valuta;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Entry.Status getStatus() {
        return status;
    }

    public void setStatus(Entry.Status status) {
        this.status = status;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String toString() {
        return "EntryDto{" +
                "id=" + id +
                ", date=" + date +
                ", valuta=" + valuta +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", memo='" + memo + '\'' +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", balance=" + balance +
                ", accountId=" + accountId +
                ", accountName='" + accountName + '\'' +
                '}';
    }

}
