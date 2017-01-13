package name.gyger.jmoney.report;

public class Balance {

    private String accountName;
    private long balance;
    private boolean total;

    public Balance(String accountName, long balance, boolean total) {
        this.accountName = accountName;
        this.balance = balance;
        this.total = total;
    }

    public String getAccountName() {
        return accountName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isTotal() {
        return total;
    }

}
