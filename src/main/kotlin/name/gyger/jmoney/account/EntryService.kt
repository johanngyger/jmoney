package name.gyger.jmoney.account

import name.gyger.jmoney.category.Category
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Service
@Transactional
open class EntryService(private val accountRepository: AccountRepository,
                        private val entryRepository: EntryRepository) {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun getEntries(accountId: Long, pageParam: Int?, filter: String?): List<Entry> {
        // TODO: Use pageable/sortable repository
        var balance = accountRepository.findOne(accountId)?.startBalance ?: 0
        var entries = entryRepository.findEntriesForAccount(accountId)
                .filter { e -> e.contains(filter) }
                .map { e ->
                    balance += e.amount
                    e.balance = balance
                    e
                }
                .reversed()

        val page = pageParam ?: 1
        val count = entries.size
        val from = Math.min((page - 1) * 10, count)
        val to = Math.min((page - 1) * 10 + 10, count)
        entries = entries.subList(from, to)

        entries.forEach {
            it.accountId = it.account?.id ?: 0
        }

        return entries
    }

    fun getEntry(id: Long): Entry {
        val entry = entryRepository.findOne(id)
        entry.accountId = entry.account?.id ?: 0
        entry.categoryId = entry.category?.id ?: 0
        return entry
    }

    fun deepSave(entry: Entry): Entry {
        entry.account = accountRepository.findOne(entry.accountId)
        entry.category = em.find(Category::class.java, entry.categoryId)

        updateSubEntries(entry)
        updateTransferEntry(entry)

        return entryRepository.save(entry)
    }

    private fun updateSubEntries(entry: Entry) {
        val oldSubEntries = entryRepository.findSubEntries(entry.id)
        if (entry.category?.type == Category.Type.SPLIT) {
            entryRepository.delete(oldSubEntries - entry.subEntries)
            createSubEntries(entry)
        } else {
            entryRepository.delete(entry.subEntries)
        }
    }

    private fun updateTransferEntry(entry: Entry) {
        if (entry.category is Account) {
            updateOtherEntry(entry)
        } else {
            removeOtherEntry(entry)
        }
    }

    private fun removeOtherEntry(entry: Entry) {
        val other = entry.other
        if (other != null) {
            entry.other = null
            other.other = null
            entryRepository.delete(other)
        }
    }

    private fun updateOtherEntry(entry: Entry) {
        val other: Entry = entry.other ?: Entry()

        other.other = entry
        other.category = entry.account
        other.account = entry.category as Account

        entry.other = entryRepository.save(other)
    }

    private fun createSubEntries(entry: Entry) {
        entry.subEntries.forEach { subEntry ->
            subEntry.category = em.find(Category::class.java, subEntry.categoryId)
            subEntry.splitEntry = entry
            entryRepository.save(subEntry)
        }
    }

}
