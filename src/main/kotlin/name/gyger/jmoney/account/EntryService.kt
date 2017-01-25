package name.gyger.jmoney.account

import name.gyger.jmoney.category.Category
import name.gyger.jmoney.category.CategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
open class EntryService(private val accountRepository: AccountRepository,
                        private val entryRepository: EntryRepository,
                        private val categoryRepository: CategoryRepository) {

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
        entry.category = categoryRepository.findOne(entry.categoryId)

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
            subEntry.category = categoryRepository.findOne(subEntry.categoryId)
            subEntry.splitEntry = entry
            entryRepository.save(subEntry)
        }
    }

    fun getEntriesWithoutCategory(): List<Entry> {
        val entries = entryRepository.getEntriesWithoutCategory()
        updateEntryBalances(entries)
        return entries
    }

    fun getEntriesForCategory(categoryId: Long, from: Date?, to: Date?): List<Entry> {
        val entries = entryRepository.getEntriesForCategory(categoryId, from, to)
        updateEntryBalances(entries)
        return entries
    }

    private fun updateEntryBalances(entries: List<Entry>) {
        var balance = 0L
        entries.reversed().forEach { entry ->
            entry.accountId = entry.account?.id ?: 0
            balance += entry.amount
            entry.balance = balance
        }
    }

}
