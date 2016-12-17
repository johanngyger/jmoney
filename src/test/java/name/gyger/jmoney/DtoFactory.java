package name.gyger.jmoney;

import name.gyger.jmoney.account.AccountDetailsDto;
import name.gyger.jmoney.category.CategoryNodeDto;
import name.gyger.jmoney.account.EntryDetailsDto;
import name.gyger.jmoney.account.AccountService;
import name.gyger.jmoney.category.CategoryService;
import name.gyger.jmoney.account.EntryService;

import java.util.stream.IntStream;

public class DtoFactory {

    public static void createEntries(EntryDetailsDto entryDto, int count, EntryService entryService) {
        IntStream.range(0, count).forEach(i -> {
            entryService.createEntry(entryDto);
        });
    }

    public static long createAccount(String name, AccountService accountService) {
        AccountDetailsDto accountDetailsDto = new AccountDetailsDto();
        accountDetailsDto.setName(name);
        return accountService.createAccount(accountDetailsDto);
    }

    public static long createTopLevelCategory(String name, CategoryService categoryService) {
        CategoryNodeDto catA = new CategoryNodeDto();
        catA.setName(name);
        catA.setParentId(categoryService.getCategoryTree().getId());
        return categoryService.createCategory(catA);
    }

}
