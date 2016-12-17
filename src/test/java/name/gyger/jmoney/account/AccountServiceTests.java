package name.gyger.jmoney.account;

import name.gyger.jmoney.session.SessionService;
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
        em.flush();
        em.clear();
    }

    @Test
    public void testBasics() {
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
        assertThat(accountService.getAccounts()).hasSize(1);

        em.flush();
        em.clear();

        accountDetailsDto.setName("B");
        accountService.createAccount(accountDetailsDto);

        assertThat(accountService.getAccounts()).hasSize(2);
    }

}
