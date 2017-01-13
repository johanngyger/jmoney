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

    @OneToMany(mappedBy = "session", cascade = CascadeType.REMOVE)
    private List<Account> accounts;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Category rootCategory;

    @OneToOne
    private Category transferCategory;

    @OneToOne
    private Category splitCategory;

    public long getId() {
        return id;
    }

    public List<Account> getAccounts() {
        return accounts;
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
