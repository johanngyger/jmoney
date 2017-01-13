package name.gyger.jmoney.account;

import name.gyger.jmoney.session.Session;
import name.gyger.jmoney.session.SessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
@Transactional
public class AccountService {

    @PersistenceContext
    private EntityManager em;

    private final SessionService sessionService;

    public AccountService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public List<Account> getAccounts() {
        List<Account> accounts = sessionService.getSession().getAccounts();
        accounts.forEach(a -> {
            em.detach(a);
            a.setSession(null);
        });
        return accounts;
    }

    public Account getAccount(long accountId) {
        return em.find(Account.class, accountId);
    }

    public long createAccount(Account a) {
        Session s = sessionService.getSession();
        a.setSession(s);
        a.setParent(s.getTransferCategory());
        em.persist(a);
        return a.getId();
    }

    public void updateAccount(Account account) {
        em.merge(account);
    }

    public void deleteAccount(long accountId) {
        Account a = em.find(Account.class, accountId);
        em.remove(a);
    }

}
