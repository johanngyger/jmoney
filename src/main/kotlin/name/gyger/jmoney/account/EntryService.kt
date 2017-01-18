package name.gyger.jmoney.account

import name.gyger.jmoney.category.Category
import name.gyger.jmoney.category.Category.Type
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Query
import javax.persistence.TypedQuery
import java.util.Collections
import java.util.stream.Collectors

@Service
@Transactional
class EntryService(private val accountService: AccountService) {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun getEntryCount(accountId: Long): Long {
        val q = em.createQuery("SELECT count(e) FROM Entry e WHERE e.account.id = :id")
        q.setParameter("id", accountId)
        return q.singleResult as Long
    }

    fun getEntries(accountId: Long, pageParam: Int?, filter: String?): List<Entry> {
        val q = em.createQuery("SELECT e FROM Entry e LEFT JOIN FETCH e.category WHERE e.account.id = :id" + " ORDER BY CASE WHEN e.date IS NULL THEN 1 ELSE 0 END, e.date, e.creation", Entry::class.java)
        q.setParameter("id", accountId)

        val balance = longArrayOf(accountService.getAccount(accountId)!!.startBalance)
        var entries = q.resultList
                .filter { e -> e.contains(filter) }
                .map { e ->
                    balance[0] += e.amount
                    e.balance = balance[0]
                    e
                }
                .reversed()

        var page = pageParam ?: 1
        val count = entries.size
        val from = Math.min((page - 1) * 10, count)
        val to = Math.min((page - 1) * 10 + 10, count)
        entries = entries.subList(from, to)

        entries.forEach {
            it.accountId = it.account!!.id
            em.detach(it)
        }

        return entries
    }

    fun getEntry(id: Long): Entry {
        val entry = em.find(Entry::class.java, id)
        entry.accountId = entry.account?.id ?: 0
        entry.categoryId = entry.category?.id ?: 0
        em.detach(entry)
        return entry
    }

    fun createEntry(entry: Entry): Long {
        em.detach(entry)
        entry.id = 0
        em.persist(entry)
        updateEntryInternal(entry)
        return entry.id
    }

    fun updateEntry(entry: Entry) {
        updateEntryInternal(entry)
        em.merge(entry)
    }

    private fun updateEntryInternal(e: Entry) {
        e.account = em.find(Account::class.java, e.accountId)
        e.category = em.find(Category::class.java, e.categoryId)

        removeOldSubEntries(e)
        if (e.category?.type == Category.Type.SPLIT) {
            createSubEntries(e)
        }

        if (e.category is Account) {
            var other: Entry? = e.other
            if (other == null) {
                other = Entry()
                em.persist(other)
            }

            other.other = e
            other.category = e.account
            other.account = e.category as Account
            e.other = other
        } else {
            val other = e.other
            e.other = null

            if (other != null) {
                other.other = null
                em.remove(other)
            }
        }
    }

    private fun removeOldSubEntries(e: Entry) {
        val q = em.createQuery("SELECT e FROM Entry e WHERE e.splitEntry.id = :id", Entry::class.java)
        q.setParameter("id", e.id)
        val oldSubEntries = q.resultList
        val newSubEntries = e.subEntries
        oldSubEntries.forEach { subEntry ->
            if (!newSubEntries!!.contains(subEntry)) {
                em.remove(subEntry)
            }
        }
    }

    private fun createSubEntries(e: Entry) {
        e.subEntries?.forEach {
            val subEntry = em.merge(it)
            subEntry.category = em.find(Category::class.java, subEntry.categoryId)
            subEntry.splitEntry = e
        }
    }

    fun deleteEntry(entryId: Long) {
        val e = em.find(Entry::class.java, entryId)
        em.remove(e)
    }
}
