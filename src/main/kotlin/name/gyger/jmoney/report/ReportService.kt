package name.gyger.jmoney.report

import name.gyger.jmoney.account.Entry
import name.gyger.jmoney.category.Category
import name.gyger.jmoney.category.CategoryService
import name.gyger.jmoney.session.SessionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Service
@Transactional
open class ReportService(private val sessionService: SessionService, private val categoryService: CategoryService) {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun getBalances(date: Date?): List<Balance> {
        val session = sessionService.getSession()
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
        val session = sessionService.getSession()
        val resultList = ArrayList<CashFlow>()

        categoryService.prefetchCategories()
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

    private fun updateEntryBalances(entries: List<Entry>) {
        var balance = 0L
        entries.reversed().forEach { entry ->
            entry.accountId = entry.account?.id ?: 0
            balance += entry.amount
            entry.balance = balance
        }
    }

    fun getInconsistentSplitEntries(): List<Entry> {
        val splitCategory = sessionService.getSession().splitCategory

        val q = em.createQuery("SELECT e FROM Entry e WHERE e.category.id = :categoryId" + " ORDER BY " +
                "CASE WHEN e.date IS NULL THEN 0 ELSE 1 END, e.date DESC, e.creation DESC", Entry::class.java)
        q.setParameter("categoryId", splitCategory.id)

        val splitEntrySums = getSplitEntrySums()
        val entries = q.resultList
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

    fun getEntriesWithoutCategory(): List<Entry> {
        val q = em.createQuery("SELECT e FROM Entry e WHERE e.category.id = null AND e.splitEntry = null ORDER BY " +
                "CASE WHEN e.date IS NULL THEN 0 ELSE 1 END, e.date DESC, e.creation DESC", Entry::class.java)
        val entries = q.resultList
        updateEntryBalances(entries)
        return entries
    }

    fun getEntriesForCategory(categoryId: Long, from: Date?, to: Date?): List<Entry> {
        val q = em.createQuery("SELECT e FROM Entry e WHERE e.category.id = :categoryId " +
                "AND e.date >= :from AND e.date <= :to" + " ORDER BY e.date DESC, e.creation DESC", Entry::class.java)
        q.setParameter("categoryId", categoryId)
        q.setParameter("from", from)
        q.setParameter("to", to)
        val entries = q.resultList
        updateEntryBalances(entries)
        return entries
    }

    private fun getEntrySumsByAccountId(date: Date?): Map<Long, Long> {
        var queryString = "SELECT NEW name.gyger.jmoney.report.ReportItem(e.account.id, SUM(e.amount)) " +
                "FROM Entry e WHERE e.account.id IS NOT NULL"
        if (date != null) queryString += " AND e.date <= :date"
        queryString += " GROUP BY e.account.id"

        val q = em.createQuery(queryString, ReportItem::class.java)
        if (date != null) {
            q.setParameter("date", date)
        }

        return q.resultList.map { it.id to it.amount }.toMap()
    }

    private fun getEntrySumsByCategoryId(from: Date?, to: Date?): Map<Long, Long> {
        val queryString = "SELECT NEW name.gyger.jmoney.report.ReportItem(e.category.id, SUM(e.amount)) FROM Entry e " +
                " WHERE e.date >= :from AND e.date <= :to" +
                " AND e.category.id IS NOT NULL" +
                " GROUP BY e.category.id"
        val q = em.createQuery(queryString, ReportItem::class.java)
        q.setParameter("from", from)
        q.setParameter("to", to)

        return q.resultList.map { it.id to it.amount }.toMap()
    }

    fun getSplitEntrySums(): Map<Long, Long> {
        val queryString = "SELECT NEW name.gyger.jmoney.report.ReportItem(e.splitEntry.id, SUM(e.amount)) " +
                "FROM Entry e WHERE e.splitEntry.id IS NOT NULL GROUP BY e.splitEntry.id"
        val q = em.createQuery(queryString, ReportItem::class.java)
        return q.resultList.map { it.id to it.amount }.toMap()
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

