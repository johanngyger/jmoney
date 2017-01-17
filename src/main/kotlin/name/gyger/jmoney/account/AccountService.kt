package name.gyger.jmoney.account

import name.gyger.jmoney.session.SessionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Service
@Transactional
open class AccountService(private val sessionService: SessionService) {

    @PersistenceContext
    private lateinit var em: EntityManager

    fun getAccounts(): List<Account> {
        val accounts = sessionService.getSession().accounts
        accounts.forEach { a ->
            em.detach(a)
            a.session = null
        }
        return accounts
    }

    fun getAccount(accountId: Long): Account? {
        return em.find(Account::class.java, accountId)
    }

    fun createAccount(a: Account): Long {
        val s = sessionService.getSession()
        a.session = s
        a.parent = s.transferCategory
        em.persist(a)
        return a.id
    }

    fun updateAccount(account: Account) {
        em.merge(account)
    }

    fun deleteAccount(accountId: Long) {
        val a = em.find(Account::class.java, accountId)
        em.remove(a)
    }

}
