package name.gyger.jmoney.report

import name.gyger.jmoney.account.AccountService
import name.gyger.jmoney.account.Entry
import name.gyger.jmoney.account.EntryService
import name.gyger.jmoney.category.Category
import name.gyger.jmoney.category.CategoryService
import name.gyger.jmoney.createAccount
import name.gyger.jmoney.createEntries
import name.gyger.jmoney.createTopLevelCategory
import name.gyger.jmoney.session.SessionService
import name.gyger.jmoney.util.parse
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
open class ReportServiceTests {

    @Autowired
    lateinit var reportService: ReportService

    @Autowired
    lateinit var categoryService: CategoryService

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var entryService: EntryService

    @Autowired
    lateinit var sessionService: SessionService

    @PersistenceContext
    lateinit private var em: EntityManager

    @Before
    fun setup() {
        sessionService.initSession()
        em.flush()
        em.clear()
    }

    @Test
    fun testBalances() {
        val accountA = createAccount("A", 0, accountService)
        val accountB = createAccount("B", 0, accountService)

        val date = parse("2000-01-01")
        assertThat(reportService.getBalances(date)).hasSize(3)

        val entry = Entry()
        entry.accountId = accountA
        entry.date = date
        entry.amount = 15
        createEntries(entry, 10, entryService)

        entry.accountId = accountB
        entry.amount = -5
        createEntries(entry, 10, entryService)

        val balances = reportService.getBalances(date)
        assertThat(balances).hasSize(3)

        val balanceA = balances[0]
        assertThat(balanceA.balance).isEqualTo(150)
        assertThat(balanceA.isTotal).isFalse()
        assertThat(balanceA.accountName).isEqualTo("A")

        val balanceB = balances[1]
        assertThat(balanceB.balance).isEqualTo(-50)
        assertThat(balanceB.isTotal).isFalse()
        assertThat(balanceB.accountName).isEqualTo("B")

        val balanceTotal = balances[2]
        assertThat(balanceTotal.balance).isEqualTo(100)
        assertThat(balanceTotal.isTotal).isTrue()
        assertThat(balanceTotal.accountName).isEqualTo("Total")
    }

    @Test
    fun testCashFlow() {
        val accId = createAccount("A", 0, accountService)

        val catA = createTopLevelCategory("A", categoryService)
        val catB = createTopLevelCategory("B", categoryService)
        val catC = createTopLevelCategory("C", categoryService)

        val date = parse("2000-10-07")
        val entry = Entry()
        entry.accountId = accId
        entry.date = date
        entry.amount = 7
        entry.categoryId = catA
        createEntries(entry, 8, entryService)

        entry.amount = -9
        entry.categoryId = catB
        createEntries(entry, 10, entryService)

        entry.amount = 11
        entry.categoryId = catC
        createEntries(entry, 12, entryService)

        em.flush()
        em.clear()

        val from = parse("2000-01-01")
        val to = parse("2001-01-01")
        val cashFlow = reportService.getCashFlow(from, to)
        assertThat(cashFlow).hasSize(7)

        val cashFlowA = cashFlow[0]
        assertThat(cashFlowA.income).isEqualTo(56)
        assertThat(cashFlowA.expense).isNull()
        assertThat(cashFlowA.difference).isNull()
        assertThat(cashFlowA.isTotal).isFalse()

        val cashFlowB = cashFlow[2]
        assertThat(cashFlowB.income).isNull()
        assertThat(cashFlowB.expense).isEqualTo(90)
        assertThat(cashFlowB.difference).isNull()
        assertThat(cashFlowB.isTotal).isFalse()

        val cashFlowC = cashFlow[4]
        assertThat(cashFlowC.income).isEqualTo(132)
        assertThat(cashFlowC.expense).isNull()
        assertThat(cashFlowC.difference).isNull()
        assertThat(cashFlowC.isTotal).isFalse()

        val cashFlowTotal = cashFlow[6]
        assertThat(cashFlowTotal.income).isEqualTo(188)
        assertThat(cashFlowTotal.expense).isEqualTo(90)
        assertThat(cashFlowTotal.difference).isEqualTo(98)
        assertThat(cashFlowTotal.isTotal).isTrue()
    }

    @Test
    fun testInconsistentSplitEntry() {
        val accountId = createAccount("my account", 0, accountService)

        val split = categoryService.getSplitCategory()
        val entry = Entry()
        entry.categoryId = split.id
        entry.accountId = accountId
        entry.amount = 12715
        entryService.deepSave(entry)
        assertThat(reportService.getInconsistentSplitEntries()).hasSize(1)

        val subEntry = Entry()
        subEntry.amount = 12715
        val subEntries = ArrayList<Entry>()
        subEntries.add(subEntry)
        entry.subEntries = subEntries
        entryService.deepSave(entry)
        assertThat(reportService.getInconsistentSplitEntries()).hasSize(0)
    }

    @Test
    fun testEntriesWithoutCategory() {
        val accountId = createAccount("my account", 0, accountService)
        val entryDto = Entry()
        entryDto.accountId = accountId
        createEntries(entryDto, 37, entryService)
        assertThat(reportService.getEntriesWithoutCategory()).hasSize(37)
    }

    @Test
    fun testEntriesForCategory() {
        val newCat = Category()
        newCat.name = "NEW"
        newCat.parentId = categoryService.getRootCategory().id
        val newCatId = categoryService.createCategory(newCat)

        val accountId = createAccount("my account", 0, accountService)
        val date = parse("2000-10-07")
        val entry = Entry()
        entry.accountId = accountId
        entry.date = date
        entry.categoryId = newCatId
        createEntries(entry, 7, entryService)

        assertThat(reportService.getEntriesForCategory(newCatId, date, date)).hasSize(7)
    }

}
