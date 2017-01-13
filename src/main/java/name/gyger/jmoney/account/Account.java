package name.gyger.jmoney.account;

import name.gyger.jmoney.category.Category;
import name.gyger.jmoney.session.Session;

import javax.persistence.*;
import java.util.List;

@Entity
public class Account extends Category {

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<Entry> entries;

    @ManyToOne
    private Session session;

    private String currencyCode;
    private String bank;
    private String accountNumber;
    private long startBalance;
    private Long minBalance;
    private String abbreviation;

    @Column(length = 1000)
    private String comment;

    public Account() {
        setType(Type.ACCOUNT);
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getStartBalance() {
        return startBalance;
    }

    public void setStartBalance(long startBalance) {
        this.startBalance = startBalance;
    }

    public Long getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(Long minBalance) {
        this.minBalance = minBalance;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

}