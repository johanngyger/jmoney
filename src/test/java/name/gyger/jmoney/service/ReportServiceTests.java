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
        long accA = DtoFactory.createAccount("A", accountService);
        long accB = DtoFactory.createAccount("B", accountService);

        Date date = DateUtil.parse("2000-01-01");

        EntryDetailsDto entryDto = new EntryDetailsDto();
        entryDto.setAccountId(accA);
        entryDto.setDate(date);
        entryDto.setAmount(15);
        DtoFactory.createEntries(entryDto, 10, entryService);

        entryDto.setAccountId(accB);
        entryDto.setAmount(-5);
        DtoFactory.createEntries(entryDto, 10, entryService);

        List<BalanceDto> balances = reportService.getBalances(date);
        assertThat(balances).hasSize(3);
        assertThat(balances.get(0).getBalance()).isEqualTo(150);
        assertThat(balances.get(1).getBalance()).isEqualTo(-50);
        assertThat(balances.get(2).getBalance()).isEqualTo(100);
    }

    @Test
    public void testCashFlow() {
        long accId = DtoFactory.createAccount("A", accountService);

        long catA = DtoFactory.createTopLevelCategory("A", categoryService);
        long catB = DtoFactory.createTopLevelCategory("A", categoryService);
        long catC = DtoFactory.createTopLevelCategory("A", categoryService);

        Date date = DateUtil.parse("2000-10-07");
        EntryDetailsDto entryDto = new EntryDetailsDto();
        entryDto.setAccountId(accId);
        entryDto.setDate(date);
        entryDto.setAmount(7);
        entryDto.setCategoryId(catA);
        DtoFactory.createEntries(entryDto, 8, entryService);

        entryDto.setAmount(-9);
        entryDto.setCategoryId(catB);
        DtoFactory.createEntries(entryDto, 10, entryService);

        entryDto.setAmount(11);
        entryDto.setCategoryId(catC);
        DtoFactory.createEntries(entryDto, 12, entryService);

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
        long accountId = DtoFactory.createAccount("my account", accountService);
        CategoryDto split = categoryService.getSplitCategory();

        EntryDetailsDto entryDto = new EntryDetailsDto();
        entryDto.setCategoryId(split.getId());
        entryDto.setAccountId(accountId);
        entryDto.setAmount(12715);
        entryService.createEntry(entryDto);
        assertThat(reportService.getInconsistentSplitEntries()).hasSize(1);
    }

    @Test
    public void testEntriesWithoutCategory() {
        long accountId = DtoFactory.createAccount("my account", accountService);
        EntryDetailsDto entryDto = new EntryDetailsDto();
        entryDto.setAccountId(accountId);
        DtoFactory.createEntries(entryDto, 37, entryService);
        assertThat(reportService.getEntriesWithoutCategory()).hasSize(37);
    }

    @Test
    public void testEntriesForCategory() {
        CategoryNodeDto newCat = new CategoryNodeDto();
        newCat.setName("NEW");
        newCat.setParentId(categoryService.getCategoryTree().getId());
        long newCatId = categoryService.createCategory(newCat);

        long accountId = DtoFactory.createAccount("my account", accountService);
        Date d = DateUtil.parse("2000-10-07");
        EntryDetailsDto entryDto = new EntryDetailsDto();
        entryDto.setAccountId(accountId);
        entryDto.setDate(d);
        entryDto.setCategoryId(newCatId);
        DtoFactory.createEntries(entryDto, 7, entryService);

        Date from = DateUtil.parse("2000-01-01");
        Date to = DateUtil.parse("2001-01-01");
        assertThat(reportService.getEntriesForCategory(newCatId, from, to)).hasSize(7);
    }

}
