package name.gyger.jmoney.service;

import name.gyger.jmoney.dto.*;
import name.gyger.jmoney.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class EntryServiceTests {

    @Autowired
    SessionService sessionService;

    @Autowired
    EntryService entryService;

    @Autowired
    AccountService accountService;

    @Autowired
    CategoryService categoryService;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        sessionService.initSession();
        em.flush();
        em.clear();
    }

    private long createAccount(String name) {
        Collection<AccountDto> accounts = accountService.getAccounts();
        AccountDetailsDto accountDetailsDto = new AccountDetailsDto();
        accountDetailsDto.setName(name);
        return accountService.createAccount(accountDetailsDto);
    }

    @Test
    public void testBasics() {
        long accountId = createAccount("my account");
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(0);

        IntStream.range(0, 10).forEach(i -> {
            EntryDetailsDto entryDto = new EntryDetailsDto();
            entryDto.setAccountId(accountId);
            entryService.createEntry(entryDto);
        });
        List<EntryDto> entries = entryService.getEntries(accountId, null, null);
        assertThat(entries).hasSize(10);
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(10);
        assertThat(entryService.getEntriesWithoutCategory()).hasSize(10);

        entryService.deleteEntry(entries.get(9).getId());
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(9);
        assertThat(entryService.getEntriesWithoutCategory()).hasSize(9);

        CategoryNodeDto newCat = new CategoryNodeDto();
        newCat.setName("NEW");
        newCat.setParentId(categoryService.getCategoryTree().getId());
        long newCatId = categoryService.createCategory(newCat);
        Date d = DateUtil.parse("2000-10-07");
        entries = entryService.getEntries(accountId, null, null);
        entries.forEach(e -> {
            EntryDetailsDto entry = entryService.getEntry(e.getId());
            entry.setCategoryId(newCatId);
            entry.setDate(d);
            entryService.updateEntry(entry);
        });

        assertThat(entryService.getEntriesWithoutCategory()).hasSize(0);
        Date from = DateUtil.parse("2000-01-01");
        Date to = DateUtil.parse("2001-01-01");
        assertThat(entryService.getEntriesForCategory(newCatId, from, to)).hasSize(9);
    }

    @Test
    public void testSplitEntries() {
        long accountId = createAccount("my account");
        CategoryDto split = categoryService.getSplitCategory();

        EntryDetailsDto entryDto = new EntryDetailsDto();
        entryDto.setCategoryId(split.getId());
        entryDto.setAccountId(accountId);
        entryDto.setAmount(11121224);
        List<SubEntryDto> subEntries = IntStream.range(0, 7).mapToObj(i -> {
            SubEntryDto sed = new SubEntryDto();
            sed.setAmount(i);
            return sed;
        }).collect(Collectors.toList());
        entryDto.setSubEntries(subEntries);
        long entryId = entryService.createEntry(entryDto);
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(1);
        em.clear();
        em.flush();
        List<EntryDto> entries = entryService.getEntries(accountId, null, null);
        assertThat(entries).hasSize(1);

        entryDto = entryService.getEntry(entryId);
        entryService.updateEntry(entryDto);
        em.clear();
        em.flush();
        entries = entryService.getEntries(accountId, null, null);
        assertThat(entries).hasSize(1);

        entryDto = entryService.getEntry(entries.get(0).getId());
        subEntries = entryDto.getSubEntries();
        assertThat(subEntries).hasSize(7);
        assertThat(entryService.getInconsistentSplitEntries()).hasSize(1);
    }

    @Test
    public void testEmptyInconsistentSplitEntry() {
        long accountId = createAccount("my account");
        CategoryDto split = categoryService.getSplitCategory();

        EntryDetailsDto entryDto = new EntryDetailsDto();
        entryDto.setCategoryId(split.getId());
        entryDto.setAccountId(accountId);
        entryDto.setAmount(12715);
        long entryId = entryService.createEntry(entryDto);
        assertThat(entryService.getInconsistentSplitEntries()).hasSize(1);
    }

    @Test
    public void testDoubleEntries() {
        long accIdA = createAccount("A");
        long accIdB = createAccount("B");

        EntryDetailsDto entryDto = new EntryDetailsDto();
        entryDto.setAccountId(accIdA);
        entryDto.setCategoryId(accIdB);
        long entryId = entryService.createEntry(entryDto);
        assertThat(entryService.getEntryCount(accIdA)).isEqualTo(1);
        assertThat(entryService.getEntryCount(accIdB)).isEqualTo(1);

        entryDto = entryService.getEntry(entryId);
        entryDto.setCategoryId(0);
        entryService.updateEntry(entryDto);
        assertThat(entryService.getEntryCount(accIdA)).isEqualTo(1);
        assertThat(entryService.getEntryCount(accIdB)).isEqualTo(0);
    }

}