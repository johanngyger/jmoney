package name.gyger.jmoney.account

import name.gyger.jmoney.session.SessionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class AccountService(private val sessionService: SessionService,
                          private val accountRepository: AccountRepository) {

    fun createAccount(a: Account): Long {
        val s = sessionService.getSession()
        a.session = s
        a.parent = s.transferCategory
        accountRepository.save(a)
        return a.id
    }

}
