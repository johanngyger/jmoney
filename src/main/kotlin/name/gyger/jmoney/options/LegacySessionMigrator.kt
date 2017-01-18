package name.gyger.jmoney.options

import name.gyger.jmoney.account.Account
import name.gyger.jmoney.account.Entry
import name.gyger.jmoney.session.Session
import net.sf.jmoney.XMLReader
import net.sf.jmoney.model.*
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.*
import javax.persistence.EntityManager

open class LegacySessionMigrator(inputStream: InputStream, private val em: EntityManager) {
    private val session = name.gyger.jmoney.session.Session(name.gyger.jmoney.category.Category(),
            name.gyger.jmoney.category.Category(), name.gyger.jmoney.category.Category())
    private val oldSession = XMLReader.readSessionFromInputStream(inputStream)
    private val oldToNewCategoryMap = HashMap<Category, name.gyger.jmoney.category.Category>()
    private val entryToOldCategoryMap = HashMap<Entry, Category>()
    private val oldToNewDoubleEntryMap = HashMap<DoubleEntry, Entry>()


    fun importSession() {
        em.persist(session)
        mapCategoryNode(oldSession.categories.rootNode, null)
        mapRootCategoryToSession()
        mapCategoryToEntry()
        mapDoubleEntries()
    }

    private fun mapCategoryNode(node: net.sf.jmoney.model.CategoryNode, parent: name.gyger.jmoney.category.Category?) {
        val oldCat = node.category
        if (oldCat is net.sf.jmoney.model.Account) {
            mapAccount(oldCat, parent)
        } else {
            mapCategory(node, parent)
        }
    }

    private fun mapAccount(oldAcc: net.sf.jmoney.model.Account, parent: name.gyger.jmoney.category.Category?) {
        val acc = Account()
        acc.abbreviation = oldAcc.abbrevation
        acc.accountNumber = oldAcc.accountNumber
        acc.bank = oldAcc.bank
        acc.name = oldAcc.categoryName
        acc.comment = oldAcc.comment
        acc.currencyCode = oldAcc.currencyCode
        acc.minBalance = oldAcc.minBalance
        acc.startBalance = oldAcc.startBalance
        acc.session = session
        acc.parent = parent

        em.persist(acc)

        oldToNewCategoryMap.put(oldAcc, acc)

        mapEntries(oldAcc, acc)
    }

    private fun mapCategory(node: net.sf.jmoney.model.CategoryNode,
                            parent: name.gyger.jmoney.category.Category?) {
        val oldCat = node.category
        val cat = createCategory(parent, oldCat)

        (0..node.childCount - 1)
                .map { node.getChildAt(it) as CategoryNode }
                .forEach { mapCategoryNode(it, cat) }
    }

    private fun mapRootCategoryToSession() {
        val oldRootCat = oldSession.categories.rootNode.category
        val rootCat = oldToNewCategoryMap[oldRootCat]
        session.rootCategory = rootCat!!
    }

    private fun mapCategoryToEntry() {
        for ((e, oldCat) in entryToOldCategoryMap) {
            val c = oldToNewCategoryMap[oldCat]
            if (c == null) {
                val root = session.rootCategory
                createCategory(root, oldCat)
            }
            e.category = c
        }
    }

    private fun mapDoubleEntries() {
        for ((oldDe, de) in oldToNewDoubleEntryMap) {
            val otherDe = oldToNewDoubleEntryMap[oldDe.other]
            if (otherDe == null) {
                log.warn("Dangling double entry: " + oldDe.description + ", " + oldDe.fullCategoryName + ", " + oldDe.date)
            }
            de.other = otherDe
        }
    }

    private fun createCategory(parent: name.gyger.jmoney.category.Category?,
                               oldCat: net.sf.jmoney.model.Category): name.gyger.jmoney.category.Category {
        val cat = name.gyger.jmoney.category.Category()
        if (oldCat is SplitCategory) {
            cat.type = name.gyger.jmoney.category.Category.Type.SPLIT
            session.splitCategory = cat
        } else if (oldCat is TransferCategory) {
            cat.type = name.gyger.jmoney.category.Category.Type.TRANSFER
            session.transferCategory = cat
        } else if (oldCat is RootCategory) {
            cat.type = name.gyger.jmoney.category.Category.Type.ROOT
            session.transferCategory = cat
        }
        cat.name = oldCat.categoryName
        cat.parent = parent

        em.persist(cat)

        oldToNewCategoryMap.put(oldCat, cat)
        if (oldCat is SplitCategory) {
            // Workaround for redundant split category.
            val oldCat2 = oldSession!!.categories.splitNode.category
            oldToNewCategoryMap.put(oldCat2, cat)
        }
        return cat
    }

    private fun mapEntries(oldAcc: net.sf.jmoney.model.Account, acc: Account) {
        oldAcc.entries
                .map { it as net.sf.jmoney.model.Entry }
                .forEach {
                    if (it is SplittedEntry) {
                        mapSplitEntry(acc, it)
                    } else {
                        mapEntryOrDoubleEntry(acc, null, it)
                    }
                }
    }

    private fun mapSplitEntry(acc: Account, oldEntry: net.sf.jmoney.model.Entry) {
        val oldSe = oldEntry as net.sf.jmoney.model.SplittedEntry
        val splitEntry = Entry()

        mapEntry(splitEntry, acc, null, oldSe)

        for (o in oldSe.entries) {
            val oldSubEntry = o as net.sf.jmoney.model.Entry
            oldSubEntry.date = oldSe.date  // fix for wrong date from old model
            mapEntryOrDoubleEntry(null, splitEntry, oldSubEntry)
        }
    }

    private fun mapEntryOrDoubleEntry(acc: Account?, splitEntry: Entry?, oldEntry: net.sf.jmoney.model.Entry) {
        if (oldEntry is net.sf.jmoney.model.DoubleEntry) {
            mapDoubleEntry(Entry(), acc, splitEntry, oldEntry)
        } else {
            mapEntry(Entry(), acc, splitEntry, oldEntry)
        }
    }

    private fun mapDoubleEntry(doubleEntry: Entry, acc: Account?, splitEntry: Entry?, oldDe: net.sf.jmoney.model.DoubleEntry) {
        mapEntry(doubleEntry, acc, splitEntry, oldDe)
        oldToNewDoubleEntryMap.put(oldDe, doubleEntry)
    }

    private fun mapEntry(e: Entry, acc: Account?, splitEntry: Entry?, oldEntry: net.sf.jmoney.model.Entry) {
        e.account = acc
        e.splitEntry = splitEntry
        e.amount = oldEntry.amount
        e.creation = oldEntry.creation
        e.date = oldEntry.date
        e.description = oldEntry.description
        e.memo = oldEntry.memo
        e.status = entryStates[oldEntry.status]
        e.valuta = oldEntry.valuta

        // Category might not exist yet, so this is done in a second pass.
        val oldCat = oldEntry.category
        if (oldCat != null) {
            entryToOldCategoryMap.put(e, oldCat)
        }

        em.persist(e)
    }

    companion object {
        private val log = LoggerFactory.getLogger(OptionsService::class.java)
        private val entryStates = mapOf(
                Pair(0, null),
                Pair(1, Entry.Status.RECONCILING),
                Pair(2, Entry.Status.CLEARED)
        )
    }

}
