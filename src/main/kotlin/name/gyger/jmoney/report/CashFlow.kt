package name.gyger.jmoney.report

class CashFlow(
        val categoryId: Long?,
        val categoryName: String,
        val income: Long?,
        val expense: Long?,
        val difference: Long?,
        val isTotal: Boolean
)
