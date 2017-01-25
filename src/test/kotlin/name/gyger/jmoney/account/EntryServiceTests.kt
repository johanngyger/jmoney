package name.gyger.jmoney.account

import name.gyger.jmoney.category.Category
import name.gyger.jmoney.category.CategoryService
import name.gyger.jmoney.createAccount
import name.gyger.jmoney.session.SessionService
import name.gyger.jmoney.util.parse
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
open class EntryServiceTests {

    @Autowired
    lateinit var sessionService: SessionService

    @Autowired
    lateinit var entryService: EntryService

    @Autowired
    lateinit var entryRepository: EntryRepository

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var categoryService: CategoryService

    @PersistenceContext
    lateinit private var em: EntityManager

    @Before
    fun setup() {
        sessionService.initSession()
        em.flush()
        em.clear()
    }

    @Test
    fun testBasics() {
        val accountId = createAccount("my account", 1000, accountService)
        assertThat(entryRepository.count(accountId)).isEqualTo(0)
        assertThat(overallEntryCount()).isEqualTo(0)

        val myCat = Category()
        myCat.name = "My cat"
        myCat.parentId = categoryService.getRootCategory().id
        val myCatId = categoryService.createCategory(myCat)

        (0L..9L).forEach { i ->
            val entry = Entry()
            entry.accountId = accountId
            entry.amount = i
            entry.description = "My description"
            entry.categoryId = myCatId
            entry.date = parse("2016-01-0" + i)
            entryService.deepSave(entry)
        }
        em.flush()
        em.clear()
        val entries = entryService.getEntries(accountId, null, null)
        assertThat(entries).hasSize(10)
        assertThat(entryRepository.count(accountId)).isEqualTo(10)
        assertThat(overallEntryCount()).isEqualTo(10)
        assertThat(entries[0].category?.id).isEqualTo(myCatId)
        assertThat(entries[0].balance).isEqualTo(1045)

        entryRepository.delete(entries[9].id)
        assertThat(entryRepository.count(accountId)).isEqualTo(9)
        assertThat(overallEntryCount()).isEqualTo(9)

        assertThat(entryService.getEntries(accountId, 1, "description")).hasSize(9)
        assertThat(entryService.getEntries(accountId, 1, "foobar")).isEmpty()
    }

    @Test
    fun testSplitEntry() {
        val accountId = createAccount("my account", 1000, accountService)
        val split = categoryService.getSplitCategory()

        var entry = Entry()
        entry.categoryId = split.id
        entry.accountId = accountId
        entry.amount = 11121224

        var subEntries = (1L..7L).map { i ->
            val subEntry = Entry()
            subEntry.amount = i
            subEntry
        }.toMutableList()
        entry.subEntries = subEntries
        val entryId = entryService.deepSave(entry).id
        em.flush()
        em.clear()
        entry = entryService.getEntry(entryId)
        assertThat(overallEntryCount()).isEqualTo(8)
        assertThat(entryRepository.count(accountId)).isEqualTo(1)
        assertThat(entryService.getEntries(accountId, null, null)).hasSize(1)
        subEntries = entry.subEntries
        assertThat(subEntries).hasSize(7)
        val firstSubEntry = subEntries[0]
        assertThat(firstSubEntry.splitEntry).isEqualTo(entry)
        assertThat(entryService.getEntry(firstSubEntry.id)).isEqualTo(firstSubEntry)

        entry.subEntries.removeAt(0)
        entryService.deepSave(entry)
        em.flush()
        em.clear()
        entry = entryService.getEntry(entryId)
        assertThat(overallEntryCount()).isEqualTo(7)
        assertThat(entryService.getEntries(accountId, null, null)).hasSize(1)
        assertThat(entry.subEntries).hasSize(6)

        entryRepository.delete(entry.id)
        em.flush()
        em.clear()
        assertThat(overallEntryCount()).isZero()
        assertThat(entryService.getEntries(accountId, null, null)).isEmpty()
    }

    @Test
    fun testDoubleEntryCreateDelete() {
        val accIdA = createAccount("A", 0, accountService)
        val accIdB = createAccount("B", 0, accountService)

        val entry = Entry()
        entry.accountId = accIdA
        entry.categoryId = accIdB
        val entryId = entryService.deepSave(entry)
        em.flush()
        em.clear()
        assertThat(entryRepository.count(accIdA)).isEqualTo(1)
        assertThat(entryRepository.count(accIdB)).isEqualTo(1)
        assertThat(overallEntryCount()).isEqualTo(2)

        entryRepository.delete(entryId)
        em.flush()
        em.clear()
        assertThat(entryRepository.count(accIdA)).isZero()
        assertThat(entryRepository.count(accIdB)).isZero()
        assertThat(overallEntryCount()).isZero()
    }

    @Test
    fun testDoubleEntryCreateUpdate() {
        val accIdA = createAccount("A", 0, accountService)
        val accIdB = createAccount("B", 0, accountService)

        var entry = Entry()
        entry.accountId = accIdA
        entry.categoryId = accIdB
        val entryId = entryService.deepSave(entry).id
        em.flush()
        em.clear()
        assertThat(entryRepository.count(accIdA)).isEqualTo(1)
        assertThat(entryRepository.count(accIdB)).isEqualTo(1)
        assertThat(overallEntryCount()).isEqualTo(2)

        entry = entryService.getEntry(entryId)
        entryService.deepSave(entry)
        em.flush()
        em.clear()
        assertThat(overallEntryCount()).isEqualTo(2)

        entry = entryService.getEntry(entryId)
        entry.categoryId = 0
        entryService.deepSave(entry)
        em.flush()
        em.clear()
        assertThat(overallEntryCount()).isEqualTo(1)
        assertThat(entryRepository.count(accIdA)).isEqualTo(1)
        assertThat(entryRepository.count(accIdB)).isEqualTo(0)

    }

    private fun overallEntryCount(): Long? {
        return em.createQuery("SELECT count(*) FROM Entry").singleResult as Long
    }

}