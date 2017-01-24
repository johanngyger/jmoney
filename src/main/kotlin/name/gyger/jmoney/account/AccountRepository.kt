package name.gyger.jmoney.account

import org.springframework.data.repository.CrudRepository

interface AccountRepository : CrudRepository<Account, Long>