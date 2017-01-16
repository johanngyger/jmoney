package name.gyger.jmoney.report;

import name.gyger.jmoney.EntityFactoryKt;
import name.gyger.jmoney.account.AccountService;
import name.gyger.jmoney.account.Entry;
import name.gyger.jmoney.account.EntryService;
import name.gyger.jmoney.category.Category;
import name.gyger.jmoney.category.CategoryService;
import name.gyger.jmoney.session.SessionService;
import name.gyger.jmoney.util.DateUtilKt;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
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
        long accountA = EntityFactoryKt.createAccount("A", 0, accountService);
        long accountB = EntityFactoryKt.createAccount("B", 0, accountService);

        Date date = DateUtilKt.parse("2000-01-01");
        assertThat(reportService.getBalances(date)).hasSize(3);

        Entry entry = new Entry();
        entry.setAccountId(accountA);
        entry.setDate(date);
        entry.setAmount(15);
        EntityFactoryKt.createEntries(entry, 10, entryService);

        entry.setAccountId(accountB);
        entry.setAmount(-5);
        EntityFactoryKt.createEntries(entry, 10, entryService);

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
        long accId = EntityFactoryKt.createAccount("A", 0, accountService);

        long catA = EntityFactoryKt.createTopLevelCategory("A", categoryService);
        long catB = EntityFactoryKt.createTopLevelCategory("B", categoryService);
        long catC = EntityFactoryKt.createTopLevelCategory("C", categoryService);
        long catD = EntityFactoryKt.createTopLevelCategory("D", categoryService);

        Date date = DateUtilKt.parse("2000-10-07");
        Entry entry = new Entry();
        entry.setAccountId(accId);
        entry.setDate(date);
        entry.setAmount(7);
        entry.setCategoryId(catA);
        EntityFactoryKt.createEntries(entry, 8, entryService);

        entry.setAmount(-9);
        entry.setCategoryId(catB);
        EntityFactoryKt.createEntries(entry, 10, entryService);

        entry.setAmount(11);
        entry.setCategoryId(catC);
        EntityFactoryKt.createEntries(entry, 12, entryService);

        em.flush();
        em.clear();

        Date from = DateUtilKt.parse("2000-01-01");
        Date to = DateUtilKt.parse("2001-01-01");
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
    public void testInconsistentSplitEntry() {
        long accountId = EntityFactoryKt.createAccount("my account", 0, accountService);

        Category split = categoryService.getSplitCategory();
        Entry entry = new Entry();
        entry.setCategoryId(split.getId());
        entry.setAccountId(accountId);
        entry.setAmount(12715);
        entryService.createEntry(entry);
        assertThat(reportService.getInconsistentSplitEntries()).hasSize(1);

        Entry subEntry = new Entry();
        subEntry.setAmount(12715);
        List<Entry> subEntries = new ArrayList<>();
        subEntries.add(subEntry);
        entry.setSubEntries(subEntries);
        entryService.updateEntry(entry);
        assertThat(reportService.getInconsistentSplitEntries()).hasSize(0);
    }

    @Test
    public void testEntriesWithoutCategory() {
        long accountId = EntityFactoryKt.createAccount("my account", 0, accountService);
        Entry entryDto = new Entry();
        entryDto.setAccountId(accountId);
        EntityFactoryKt.createEntries(entryDto, 37, entryService);
        assertThat(reportService.getEntriesWithoutCategory()).hasSize(37);
    }

    @Test
    public void testEntriesForCategory() {
        Category newCat = new Category();
        newCat.setName("NEW");
        newCat.setParentId(categoryService.getRootCategory().getId());
        long newCatId = categoryService.createCategory(newCat);

        long accountId = EntityFactoryKt.createAccount("my account", 0, accountService);
        Date date = DateUtilKt.parse("2000-10-07");
        Entry entry = new Entry();
        entry.setAccountId(accountId);
        entry.setDate(date);
        entry.setCategoryId(newCatId);
        EntityFactoryKt.createEntries(entry, 7, entryService);

        assertThat(reportService.getEntriesForCategory(newCatId, date, date)).hasSize(7);
    }

}
