package name.gyger.jmoney.account

import name.gyger.jmoney.session.SessionService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
open class AccountServiceTests {

    @Autowired
    lateinit var sessionService: SessionService

    @Autowired
    lateinit var accountService: AccountService

    @PersistenceContext
    lateinit private var em: EntityManager

    @Before
    fun setup() {
        sessionService.initSession()
        em.flush()
        em.clear()
    }

    @Test
    fun testBasics() {
        val accounts = accountService.getAccounts()
        assertThat(accounts).isEmpty()

        val accountDetailsDto = Account()
        accountDetailsDto.name = "my account"
        val id = accountService.createAccount(accountDetailsDto)

        var account: Account? = accountService.getAccount(id)
        assertThat(account?.id).isEqualTo(id)
        assertThat(account?.name).isEqualTo("my account")

        account = accountService.getAccount(id)
        assertThat(accountDetailsDto.id).isEqualTo(id)
        assertThat(accountDetailsDto.name).isEqualTo("my account")

        account?.name = "my other account"
        accountService.updateAccount(account!!)

        account = accountService.getAccount(id)
        assertThat(account?.name).isEqualTo("my other account")

        accountService.deleteAccount(id)
        assertThat(accountService.getAccount(id)).isNull()
    }

    @Test
    fun testMultipleAccounts() {
        var accountDetailsDto: Account

        accountDetailsDto = Account()
        accountDetailsDto.name = "A"
        val acctA = accountService.createAccount(accountDetailsDto)
        assertThat(accountService.getAccount(acctA)).isNotNull()
        assertThat(overallAccountCount()).isEqualTo(1)
        assertThat(accountService.getAccounts()).hasSize(1)

        accountDetailsDto = Account()
        accountDetailsDto.name = "B"
        val acctB = accountService.createAccount(accountDetailsDto)
        em.flush()
        em.clear()
        assertThat(accountService.getAccount(acctB)).isNotNull()
        assertThat(overallAccountCount()).isEqualTo(2)
        assertThat(accountService.getAccounts()).hasSize(2)
    }

    private fun overallAccountCount(): Long? {
        return em.createQuery("SELECT count(*) FROM Account").singleResult as Long
    }

}
