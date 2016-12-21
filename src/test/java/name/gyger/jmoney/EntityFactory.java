package name.gyger.jmoney;

import name.gyger.jmoney.account.AccountDetailsDto;
import name.gyger.jmoney.account.AccountService;
import name.gyger.jmoney.account.Entry;
import name.gyger.jmoney.account.EntryService;
import name.gyger.jmoney.category.CategoryNodeDto;
import name.gyger.jmoney.category.CategoryService;

import java.util.stream.IntStream;

public class EntityFactory {

    public static void createEntries(Entry entry, int count, EntryService entryService) {
        IntStream.range(0, count).forEach(i -> {
            entryService.createEntry(entry);
        });
    }

    public static long createAccount(String name, long startBalance, AccountService accountService) {
        AccountDetailsDto accountDetailsDto = new AccountDetailsDto();
        accountDetailsDto.setName(name);
        accountDetailsDto.setStartBalance(startBalance);
        return accountService.createAccount(accountDetailsDto);
    }

    public static long createTopLevelCategory(String name, CategoryService categoryService) {
        CategoryNodeDto catA = new CategoryNodeDto();
        catA.setName(name);
        catA.setParentId(categoryService.getCategoryTree().getId());
        return categoryService.createCategory(catA);
    }

}
