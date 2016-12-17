package name.gyger.jmoney.service;

import name.gyger.jmoney.dto.CategoryDto;
import name.gyger.jmoney.dto.EntryDetailsDto;
import name.gyger.jmoney.dto.EntryDto;
import name.gyger.jmoney.dto.SubEntryDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
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

    @Test
    public void testBasics() {
        long accountId = DtoFactory.createAccount("my account", accountService);
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
        long accountId = DtoFactory.createAccount("my account", accountService);
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
    }

    @Test
    public void testDoubleEntries() {
        long accIdA = DtoFactory.createAccount("A", accountService);
        long accIdB = DtoFactory.createAccount("B", accountService);

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