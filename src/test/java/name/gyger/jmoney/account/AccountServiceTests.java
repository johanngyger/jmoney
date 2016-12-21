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
        em.flush();
        em.clear();
    }

    @Test
    public void testBasics() {
        List<Account> accounts = accountService.getAccounts();
        assertThat(accounts).isEmpty();

        Account accountDetailsDto = new Account();
        accountDetailsDto.setName("my account");
        long id = accountService.createAccount(accountDetailsDto);

        Account account = accountService.getAccount(id);
        assertThat(account.getId()).isEqualTo(id);
        assertThat(account.getName()).isEqualTo("my account");

        account = accountService.getAccount(id);
        assertThat(accountDetailsDto.getId()).isEqualTo(id);
        assertThat(accountDetailsDto.getName()).isEqualTo("my account");

        account.setName("my other account");
        accountService.updateAccount(account);

        account = accountService.getAccount(id);
        assertThat(account.getName()).isEqualTo("my other account");

        accountService.deleteAccount(id);
        assertThat(accountService.getAccount(id)).isNull();
    }

    @Test
    public void testMultipleAccounts() {
        Account accountDetailsDto;

        accountDetailsDto = new Account();
        accountDetailsDto.setName("A");
        long acctA = accountService.createAccount(accountDetailsDto);
        assertThat(accountService.getAccount(acctA)).isNotNull();
        assertThat(overallAccountCount()).isEqualTo(1);
        assertThat(accountService.getAccounts()).hasSize(1);

        accountDetailsDto = new Account();
        accountDetailsDto.setName("B");
        long acctB = accountService.createAccount(accountDetailsDto);
        em.flush();
        em.clear();
        assertThat(accountService.getAccount(acctB)).isNotNull();
        assertThat(overallAccountCount()).isEqualTo(2);
        assertThat(accountService.getAccounts()).hasSize(2);
    }

    private Long overallAccountCount() {
        return (Long) em.createQuery("SELECT count(*) FROM Account").getSingleResult();
    }

}
