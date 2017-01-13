package name.gyger.jmoney.account;

import name.gyger.jmoney.EntityFactory;
import name.gyger.jmoney.category.Category;
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
        long accountId = EntityFactory.createAccount("my account", 1000, accountService);
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(0);
        assertThat(overallEntryCount()).isEqualTo(0);

        Category myCat = new Category();
        myCat.setName("My cat");
        myCat.setParentId(categoryService.getRootCategory().getId());
        long myCatId = categoryService.createCategory(myCat);

        IntStream.range(0, 10).forEach(i -> {
            Entry entry = new Entry();
            entry.setAccountId(accountId);
            entry.setAmount(i);
            entry.setDescription("My description");
            entry.setCategoryId(myCatId);
            entry.setDate(DateUtil.parse("2016-01-0" + i));
            entryService.createEntry(entry);
        });
        em.flush();
        em.clear();
        List<Entry> entries = entryService.getEntries(accountId, null, null);
        assertThat(entries).hasSize(10);
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(10);
        assertThat(overallEntryCount()).isEqualTo(10);
        assertThat(entries.get(0).getCategory().getId()).isEqualTo(myCatId);
        assertThat(entries.get(0).getBalance()).isEqualTo(1045);

        entryService.deleteEntry(entries.get(9).getId());
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(9);
        assertThat(overallEntryCount()).isEqualTo(9);

        assertThat(entryService.getEntries(accountId, null, "description")).hasSize(9);
        assertThat(entryService.getEntries(accountId, null, "foobar")).isEmpty();
    }

    @Test
    public void testSplitEntry() {
        long accountId = EntityFactory.createAccount("my account", 1000, accountService);
        Category split = categoryService.getSplitCategory();

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
        em.flush();
        em.clear();
        entry = entryService.getEntry(entryId);
        assertThat(overallEntryCount()).isEqualTo(8);
        assertThat(entryService.getEntryCount(accountId)).isEqualTo(1);
        assertThat(entryService.getEntries(accountId, null, null)).hasSize(1);
        subEntries = entry.getSubEntries();
        assertThat(subEntries).hasSize(7);
        assertThat(subEntries.get(0).getSplitEntry()).isEqualTo(entry);

        entry.getSubEntries().remove(0);
        entryService.updateEntry(entry);
        em.flush();
        em.clear();
        entry = entryService.getEntry(entryId);
        assertThat(overallEntryCount()).isEqualTo(7);
        assertThat(entryService.getEntries(accountId, null, null)).hasSize(1);
        assertThat(entry.getSubEntries()).hasSize(6);

        entryService.deleteEntry(entry.getId());
        em.flush();
        em.clear();
        assertThat(overallEntryCount()).isZero();
        assertThat(entryService.getEntries(accountId, null, null)).isEmpty();
    }

    @Test
    public void testDoubleEntryCreateDelete() {
        long accIdA = EntityFactory.createAccount("A", 0, accountService);
        long accIdB = EntityFactory.createAccount("B", 0, accountService);

        Entry entry = new Entry();
        entry.setAccountId(accIdA);
        entry.setCategoryId(accIdB);
        long entryId = entryService.createEntry(entry);
        em.flush();
        em.clear();
        assertThat(entryService.getEntryCount(accIdA)).isEqualTo(1);
        assertThat(entryService.getEntryCount(accIdB)).isEqualTo(1);
        assertThat(overallEntryCount()).isEqualTo(2);

        entryService.deleteEntry(entryId);
        em.flush();
        em.clear();
        assertThat(entryService.getEntryCount(accIdA)).isZero();
        assertThat(entryService.getEntryCount(accIdB)).isZero();
        assertThat(overallEntryCount()).isZero();
    }

    @Test
    public void testDoubleEntryCreateUpdate() {
        long accIdA = EntityFactory.createAccount("A", 0, accountService);
        long accIdB = EntityFactory.createAccount("B", 0, accountService);

        Entry entry = new Entry();
        entry.setAccountId(accIdA);
        entry.setCategoryId(accIdB);
        long entryId = entryService.createEntry(entry);
        em.flush();
        em.clear();
        assertThat(entryService.getEntryCount(accIdA)).isEqualTo(1);
        assertThat(entryService.getEntryCount(accIdB)).isEqualTo(1);
        assertThat(overallEntryCount()).isEqualTo(2);

        entry = entryService.getEntry(entryId);
        entry.setCategoryId(0);
        entryService.updateEntry(entry);
        em.flush();
        em.clear();
        assertThat(overallEntryCount()).isEqualTo(1);
        assertThat(entryService.getEntryCount(accIdA)).isEqualTo(1);
        assertThat(entryService.getEntryCount(accIdB)).isEqualTo(0);
    }

    private Long overallEntryCount() {
        return (Long) em.createQuery("SELECT count(*) FROM Entry").getSingleResult();
    }

}