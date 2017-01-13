package name.gyger.jmoney.report;

public class CashFlow {

    private Long categoryId;
    private String categoryName;
    private Long income;
    private Long expense;
    private Long difference;
    private boolean total;

    public CashFlow(Long categoryId, String categoryName, Long income, Long expense, Long difference, boolean total) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.income = income;
        this.expense = expense;
        this.difference = difference;
        this.total = total;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean isTotal() {
        return total;
    }

    public Long getIncome() {
        return income;
    }

    public Long getExpense() {
        return expense;
    }

    public Long getDifference() {
        return difference;
    }

    public Long getCategoryId() {
        return categoryId;
    }

}
