package name.gyger.jmoney.account;

import name.gyger.jmoney.DtoFactory;
import name.gyger.jmoney.category.CategoryDto;
import name.gyger.jmoney.category.CategoryService;
import name.gyger.jmoney.session.SessionService;
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
        long accountId = DtoFactory.createAccount("my account", 1000, accountService);
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(0);

        IntStream.range(0, 10).forEach(i -> {
            Entry entry = new Entry();
            entry.setAccountId(accountId);
            entry.setAmount(i);
            entry.setDate(DateUtil.parse("2016-01-0" + i));
            entryService.createEntry(entry);
        });
        List<Entry> entries = entryService.getEntries(accountId, null, null);
        assertThat(entries).hasSize(10);
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(10);
        assertThat(entries.get(0).getBalance()).isEqualTo(1045);

        entryService.deleteEntry(entries.get(9).getId());
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(9);
    }

    @Test
    public void testSplitEntries() {
        long accountId = DtoFactory.createAccount("my account", 1000, accountService);
        CategoryDto split = categoryService.getSplitCategory();

        Entry entry = new Entry();
        entry.setCategoryId(split.getId());
        entry.setAccountId(accountId);
        entry.setAmount(11121224);
        List<Entry> subEntries = IntStream.range(0, 7).mapToObj(i -> {
            Entry subEntry = new Entry();
            subEntry.setAmount(i);
            return subEntry;
        }).collect(Collectors.toList());
        entry.setSubEntries(subEntries);
        long entryId = entryService.createEntry(entry);
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(1);
        em.clear();
        em.flush();
        List<Entry> entries = entryService.getEntries(accountId, null, null);
        assertThat(entries).hasSize(1);

        entry = entryService.getEntry(entryId);
        entryService.updateEntry(entry);
        em.clear();
        em.flush();
        entries = entryService.getEntries(accountId, null, null);
        assertThat(entries).hasSize(1);

        entry = entryService.getEntry(entries.get(0).getId());
        subEntries = entry.getSubEntries();
        assertThat(subEntries).hasSize(7);
    }

    @Test
    public void testDoubleEntries() {
        long accIdA = DtoFactory.createAccount("A", 0, accountService);
        long accIdB = DtoFactory.createAccount("B", 0, accountService);

        Entry entry = new Entry();
        entry.setAccountId(accIdA);
        entry.setCategoryId(accIdB);
        long entryId = entryService.createEntry(entry);
        assertThat(entryService.getEntryCount(accIdA)).isEqualTo(1);
        assertThat(entryService.getEntryCount(accIdB)).isEqualTo(1);

        entry = entryService.getEntry(entryId);
        entry.setCategoryId(0);
        entryService.updateEntry(entry);
        assertThat(entryService.getEntryCount(accIdA)).isEqualTo(1);
        assertThat(entryService.getEntryCount(accIdB)).isEqualTo(0);
    }

}