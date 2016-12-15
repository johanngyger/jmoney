package name.gyger.jmoney.service;

import name.gyger.jmoney.dto.*;
import name.gyger.jmoney.model.Account;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

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

    @Test
    public void testBasics() {
        Collection<AccountDto> accounts = accountService.getAccounts();
        AccountDetailsDto accountDetailsDto = new AccountDetailsDto();
        accountDetailsDto.setName("my account");
        long accountId = accountService.createAccount(accountDetailsDto);
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(0);

        IntStream.range(0, 10).forEach(i -> {
            EntryDetailsDto entryDto = new EntryDetailsDto();
            entryDto.setAccountId(accountId);
            entryService.createEntry(entryDto);
        });
        List<EntryDto> entries = entryService.getEntries(accountId, null, null);
        assertThat(entries).hasSize(10);
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(10);

        entryService.deleteEntry(entries.get(9).getId());
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(9);
    }

    @Test
    public void testSplitEntries() {
        Collection<AccountDto> accounts = accountService.getAccounts();
        AccountDetailsDto accountDetailsDto = new AccountDetailsDto();
        accountDetailsDto.setName("my account");
        long accountId = accountService.createAccount(accountDetailsDto);
        CategoryDto split = categoryService.getSplitCategory();

        EntryDetailsDto entryDto = new EntryDetailsDto();
        entryDto.setCategoryId(split.getId());
        entryDto.setAccountId(accountId);
        List<SubEntryDto> subEntries = IntStream.range(0, 7).mapToObj(i -> {
            SubEntryDto sed = new SubEntryDto();
            sed.setAmount(i);
            return sed;
        }).collect(Collectors.toList());
        entryDto.setSubEntries(subEntries);
        long id = entryService.createEntry(entryDto);
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(1);
        em.clear();
        em.flush();
        List<EntryDto> entries = entryService.getEntries(accountId, null, null);
        assertThat(entries).hasSize(1);

        entryDto = entryService.getEntry(entries.get(0).getId());
        subEntries = entryDto.getSubEntries();
        assertThat(subEntries).hasSize(7);
    }

}