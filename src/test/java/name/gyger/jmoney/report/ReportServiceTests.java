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
        long accountA = EntityFactory.createAccount("A", 0, accountService);
        long accountB = EntityFactory.createAccount("B", 0, accountService);

        Date date = DateUtil.parse("2000-01-01");

        Entry entry = new Entry();
        entry.setAccountId(accountA);
        entry.setDate(date);
        entry.setAmount(15);
        EntityFactory.createEntries(entry, 10, entryService);

        entry.setAccountId(accountB);
        entry.setAmount(-5);
        EntityFactory.createEntries(entry, 10, entryService);

        List<Balance> balances = reportService.getBalances(date);
        assertThat(balances).hasSize(3);

        Balance balanceA = balances.get(0);
        assertThat(balanceA.getBalance()).isEqualTo(150);
        assertThat(balanceA.isTotal()).isFalse();
        assertThat(balanceA.getAccountName()).isEqualTo("A");

        Balance balanceB = balances.get(1);
        assertThat(balanceB.getBalance()).isEqualTo(-50);
        assertThat(balanceB.isTotal()).isFalse();
        assertThat(balanceB.getAccountName()).isEqualTo("B");

        Balance balanceTotal = balances.get(2);
        assertThat(balanceTotal.getBalance()).isEqualTo(100);
        assertThat(balanceTotal.isTotal()).isTrue();
        assertThat(balanceTotal.getAccountName()).isEqualTo("Gesamt");
    }

    @Test
    public void testCashFlow() {
        long accId = EntityFactory.createAccount("A", 0, accountService);

        long catA = EntityFactory.createTopLevelCategory("A", categoryService);
        long catB = EntityFactory.createTopLevelCategory("B", categoryService);
        long catC = EntityFactory.createTopLevelCategory("C", categoryService);

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
        List<CashFlow> cashFlow = reportService.getCashFlow(from, to);
        assertThat(cashFlow).hasSize(7);

        CashFlow cashFlowA = cashFlow.get(0);
        assertThat(cashFlowA.getIncome()).isEqualTo(56);
        assertThat(cashFlowA.getExpense()).isNull();
        assertThat(cashFlowA.getDifference()).isNull();
        assertThat(cashFlowA.isTotal()).isFalse();

        CashFlow cashFlowB = cashFlow.get(2);
        assertThat(cashFlowB.getIncome()).isNull();
        assertThat(cashFlowB.getExpense()).isEqualTo(90);
        assertThat(cashFlowB.getDifference()).isNull();
        assertThat(cashFlowB.isTotal()).isFalse();

        CashFlow cashFlowC = cashFlow.get(4);
        assertThat(cashFlowC.getIncome()).isEqualTo(132);
        assertThat(cashFlowC.getExpense()).isNull();
        assertThat(cashFlowC.getDifference()).isNull();
        assertThat(cashFlowC.isTotal()).isFalse();

        CashFlow cashFlowTotal = cashFlow.get(6);
        assertThat(cashFlowTotal.getIncome()).isEqualTo(188);
        assertThat(cashFlowTotal.getExpense()).isEqualTo(90);
        assertThat(cashFlowTotal.getDifference()).isEqualTo(98);
        assertThat(cashFlowTotal.isTotal()).isTrue();
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
