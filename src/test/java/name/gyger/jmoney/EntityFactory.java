package name.gyger.jmoney;

import name.gyger.jmoney.account.Account;
import name.gyger.jmoney.account.AccountService;
import name.gyger.jmoney.account.Entry;
import name.gyger.jmoney.account.EntryService;
import name.gyger.jmoney.category.Category;
import name.gyger.jmoney.category.CategoryService;

import java.util.stream.IntStream;

public class EntityFactory {

    public static void createEntries(Entry entry, int count, EntryService entryService) {
        IntStream.range(0, count).forEach(i -> {
            entryService.createEntry(entry);
        });
    }

    public static long createAccount(String name, long startBalance, AccountService accountService) {
        Account account = new Account();
        account.setName(name);
        account.setStartBalance(startBalance);
        return accountService.createAccount(account);
    }

    public static long createTopLevelCategory(String name, CategoryService categoryService) {
        Category catA = new Category();
        catA.setName(name);
        catA.setParentId(categoryService.getRootCategory().getId());
        return categoryService.createCategory(catA);
    }

}
