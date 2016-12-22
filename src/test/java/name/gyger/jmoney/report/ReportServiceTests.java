package name.gyger.jmoney.report;

import name.gyger.jmoney.EntityFactory;
import name.gyger.jmoney.account.AccountService;
import name.gyger.jmoney.account.Entry;
import name.gyger.jmoney.account.EntryService;
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
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ReportServiceTests {

    @Autowired
    ReportService reportService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    AccountService accountService;

    @Autowired
    EntryService entryService;

    @Autowired
    SessionService sessionService;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        sessionService.initSession();
        em.flush();
        em.clear();
    }

    @Test
    public void testBalances() {
        long accA = EntityFactory.createAccount("A", 0, accountService);
        long accB = EntityFactory.createAccount("B", 0, accountService);

        Date date = DateUtil.parse("2000-01-01");

        Entry entry = new Entry();
        entry.setAccountId(accA);
        entry.setDate(date);
        entry.setAmount(15);
        EntityFactory.createEntries(entry, 10, entryService);

        entry.setAccountId(accB);
        entry.setAmount(-5);
        EntityFactory.createEntries(entry, 10, entryService);

        List<BalanceDto> balances = reportService.getBalances(date);
        assertThat(balances).hasSize(3);
        assertThat(balances.get(0).getBalance()).isEqualTo(150);
        assertThat(balances.get(1).getBalance()).isEqualTo(-50);
        assertThat(balances.get(2).getBalance()).isEqualTo(100);
    }

    @Test
    public void testCashFlow() {
        long accId = EntityFactory.createAccount("A", 0, accountService);

        long catA = EntityFactory.createTopLevelCategory("A", categoryService);
        long catB = EntityFactory.createTopLevelCategory("A", categoryService);
        long catC = EntityFactory.createTopLevelCategory("A", categoryService);

        Date date = DateUtil.parse("2000-10-07");
        Entry entry = new Entry();
        entry.setAccountId(accId);
        entry.setDate(date);
        entry.setAmount(7);
        entry.setCategoryId(catA);
        EntityFactory.createEntries(entry, 8, entryService);

        entry.setAmount(-9);
        entry.setCategoryId(catB);
        EntityFactory.createEntries(entry, 10, entryService);

        entry.setAmount(11);
        entry.setCategoryId(catC);
        EntityFactory.createEntries(entry, 12, entryService);

        em.flush();
        em.clear();

        Date from = DateUtil.parse("2000-01-01");
        Date to = DateUtil.parse("2001-01-01");
        List<CashFlowDto> cashFlow = reportService.getCashFlow(from, to);
        assertThat(cashFlow).hasSize(7);
        assertThat(cashFlow.get(0).getIncome()).isEqualTo(56);
        assertThat(cashFlow.get(2).getExpense()).isEqualTo(90);
        assertThat(cashFlow.get(4).getIncome()).isEqualTo(132);
        assertThat(cashFlow.get(6).getIncome()).isEqualTo(188);
    }

    @Test
    public void testEmptyInconsistentSplitEntry() {
        long accountId = EntityFactory.createAccount("my account", 0, accountService);
        Category split = categoryService.getSplitCategory();

        Entry entry = new Entry();
        entry.setCategoryId(split.getId());
        entry.setAccountId(accountId);
        entry.setAmount(12715);
        entryService.createEntry(entry);
        assertThat(reportService.getInconsistentSplitEntries()).hasSize(1);
    }

    @Test
    public void testEntriesWithoutCategory() {
        long accountId = EntityFactory.createAccount("my account", 0, accountService);
        Entry entryDto = new Entry();
        entryDto.setAccountId(accountId);
        EntityFactory.createEntries(entryDto, 37, entryService);
        assertThat(reportService.getEntriesWithoutCategory()).hasSize(37);
    }

    @Test
    public void testEntriesForCategory() {
        Category newCat = new Category();
        newCat.setName("NEW");
        newCat.setParentId(categoryService.getRootCategory().getId());
        long newCatId = categoryService.createCategory(newCat);

        long accountId = EntityFactory.createAccount("my account", 0, accountService);
        Date date = DateUtil.parse("2000-10-07");
        Entry entry = new Entry();
        entry.setAccountId(accountId);
        entry.setDate(date);
        entry.setCategoryId(newCatId);
        EntityFactory.createEntries(entry, 7, entryService);

        assertThat(reportService.getEntriesForCategory(newCatId, date, date)).hasSize(7);
    }

}
