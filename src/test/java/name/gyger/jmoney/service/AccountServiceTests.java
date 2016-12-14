package name.gyger.jmoney.service;

import name.gyger.jmoney.dto.AccountDetailsDto;
import name.gyger.jmoney.dto.AccountDto;
import name.gyger.jmoney.dto.CategoryDto;
import name.gyger.jmoney.model.Account;
import name.gyger.jmoney.model.Category;
import name.gyger.jmoney.model.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class AccountServiceTests {

    @Autowired
    SessionService sessionService;

    @Autowired
    AccountService accountService;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setup() {
        sessionService.initSession();
        em.refresh(sessionService.getSession());
    }

    @Test
    public void testAccountServices() {
        Collection<AccountDto> accounts = accountService.getAccounts();
        assertThat(accounts).isEmpty();

        AccountDetailsDto accountDetailsDto = new AccountDetailsDto();
        accountDetailsDto.setName("my account");
        long id = accountService.createAccount(accountDetailsDto);

        Account account = accountService.getAccount(id);
        assertThat(account.getId()).isEqualTo(id);
        assertThat(account.getName()).isEqualTo("my account");

        accountDetailsDto = accountService.getAccountDetails(id);
        assertThat(accountDetailsDto.getId()).isEqualTo(id);
        assertThat(accountDetailsDto.getName()).isEqualTo("my account");

        account.setName("my other account");
        accountDetailsDto = new AccountDetailsDto(account);
        accountService.updateAccount(accountDetailsDto);

        account = accountService.getAccount(id);
        assertThat(account.getName()).isEqualTo("my other account");

        accountService.deleteAccount(id);
        assertThat(accountService.getAccount(id)).isNull();
    }

    @Test
    public void testMultipleAccounts() {
        AccountDetailsDto accountDetailsDto = new AccountDetailsDto();

        accountDetailsDto.setName("A");
        accountService.createAccount(accountDetailsDto);

        accountDetailsDto.setName("B");
        accountService.createAccount(accountDetailsDto);

        assertThat(accountService.getAccounts()).hasSize(2);
    }

}
