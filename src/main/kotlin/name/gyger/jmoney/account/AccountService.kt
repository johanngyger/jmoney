package name.gyger.jmoney.account

import name.gyger.jmoney.session.SessionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccountService(private val sessionRepository: SessionRepository,
                     private val accountRepository: AccountRepository) {

    fun createAccount(a: Account): Long {
        val s = sessionRepository.getSession()
        a.session = s
        a.parent = s.transferCategory
        accountRepository.save(a)
        return a.id
    }

}
