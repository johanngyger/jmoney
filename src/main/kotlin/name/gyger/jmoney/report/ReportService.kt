package name.gyger.jmoney.report

import name.gyger.jmoney.account.Entry
import name.gyger.jmoney.account.EntryRepository
import name.gyger.jmoney.category.Category
import name.gyger.jmoney.category.CategoryRepository
import name.gyger.jmoney.session.SessionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
open class ReportService(private val sessionRepository: SessionRepository,
                         private val categoryRepository: CategoryRepository,
                         private val reportRepository: ReportRepository,
                         private val entryRepository: EntryRepository) {

    fun getBalances(date: Date?): List<Balance> {
        val session = sessionRepository.getSession()
        val result = ArrayList<Balance>()

        val entrySums = getEntrySumsByAccountId(date)

        var totalBalance: Long = 0
        for (account in session.accounts) {
            var balance: Long = 0
            val sum = entrySums[account.id]
            if (sum != null) {
                balance = sum
            }
            balance += account.startBalance
            totalBalance += balance

            val dto = Balance(account.name, balance, false)
            result.add(dto)
        }

        val totalBalanceDto = Balance("Total", totalBalance, true)
        result.add(totalBalanceDto)

        return result
    }

    fun getCashFlow(from: Date?, to: Date?): List<CashFlow> {
        val session = sessionRepository.getSession()
        val resultList = ArrayList<CashFlow>()

        categoryRepository.findAll()  // prefetch
        val root = session.rootCategory

        val entrySums = getEntrySumsByCategoryId(from, to)

        var totalIncome: Long = 0
        var totalExpense: Long = 0

        for (child in root.children) {
            val subList = ArrayList<CashFlow>()
            calculateCashFlowForCategory(subList, entrySums, child, null)

            var income: Long = 0
            var expense: Long = 0
            for (cashFlow in subList) {
                income += toZeroIfNull(cashFlow.income)
                expense += toZeroIfNull(cashFlow.expense)
            }


            if (income != 0L || expense != 0L) {
                val childDto = CashFlow(null, child.name + " (Total)", income, expense, income - expense, true)
                subList.add(childDto)
            }

            totalIncome += income
            totalExpense += expense
            resultList.addAll(subList)
        }

        val total = CashFlow(null, "Total", totalIncome, totalExpense, totalIncome - totalExpense, true)
        resultList.add(total)
        return resultList
    }

    fun getInconsistentSplitEntries(): List<Entry> {
        val splitCategory = sessionRepository.getSession().splitCategory
        val entries = entryRepository.getInconsistentSplitEntries(splitCategory.id)
        val splitEntrySums = getSplitEntrySums()
        val result = ArrayList<Entry>()
        for (entry in entries) {
            var sum: Long? = splitEntrySums[entry.id]
            if (sum == null) {
                sum = java.lang.Long.valueOf(0)
            }
            if (entry.amount != sum) {
                result.add(entry)
            }
            entry.accountId = entry.account?.id ?: 0
        }
        return result
    }

    private fun getEntrySumsByAccountId(date: Date?): Map<Long, Long> {
        return reportRepository.getEntrySumsByAccountId(date)
                .map { it.id to it.amount }.toMap()
    }

    private fun getEntrySumsByCategoryId(from: Date?, to: Date?): Map<Long, Long> {
        return reportRepository.getEntrySumsByCategoryId(from, to)
                .map { it.id to it.amount }.toMap()
    }

    fun getSplitEntrySums(): Map<Long, Long> {
        return reportRepository.getSplitEntrySums()
                .map { it.id to it.amount }.toMap()
    }

    private fun toZeroIfNull(value: Long?): Long {
        if (value != null) {
            return value
        } else {
            return 0
        }
    }

    private fun calculateCashFlowForCategory(resultList: MutableList<CashFlow>, entrySums: Map<Long, Long>,
                                             category: Category, parentName: String?) {
        val name = createCategoryName(category, parentName)

        if (category.type == Category.Type.NORMAL) {
            val sum = entrySums[category.id]
            createCategoryFlowDto(resultList, category.id, name, sum)
        }

        for (child in category.children) {
            calculateCashFlowForCategory(resultList, entrySums, child, name)
        }
    }

    private fun createCategoryFlowDto(resultList: MutableList<CashFlow>, id: Long?, name: String, sum: Long?) {
        if (sum != null) {
            var income: Long? = null
            var expense: Long? = null

            if (sum >= 0) {
                income = sum
            } else {
                expense = -sum
            }

            val dto = CashFlow(id, name, income, expense, null, false)
            resultList.add(dto)
        }
    }

    private fun createCategoryName(category: Category, parentName: String?): String {
        var name = category.name
        if (parentName != null) {
            name = parentName + ":" + name
        }
        return name
    }
}

