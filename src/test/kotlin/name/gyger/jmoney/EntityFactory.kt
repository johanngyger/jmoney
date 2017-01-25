package name.gyger.jmoney

import name.gyger.jmoney.account.Account
import name.gyger.jmoney.account.AccountService
import name.gyger.jmoney.account.Entry
import name.gyger.jmoney.account.EntryService
import name.gyger.jmoney.category.Category
import name.gyger.jmoney.category.CategoryService

import java.util.stream.IntStream

fun createEntries(entryProto: Entry, count: Int, entryService: EntryService) {
    IntStream.range(0, count).forEach {
        val entry = Entry()
        entry.accountId = entryProto.accountId
        entry.categoryId = entryProto.categoryId
        entry.date = entryProto.date
        entry.amount = entryProto.amount
        entryService.deepSave(entry)
    }
}

fun createAccount(name: String, startBalance: Long, accountService: AccountService): Long {
    val account = Account()
    account.name = name
    account.startBalance = startBalance
    return accountService.createAccount(account)
}

fun createTopLevelCategory(name: String, categoryService: CategoryService): Long {
    val catA = Category()
    catA.name = name
    catA.parentId = categoryService.getRootCategory().id
    return categoryService.createCategory(catA)
}

