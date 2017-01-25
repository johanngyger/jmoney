package name.gyger.jmoney.account

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/accounts")
class AccountController(private val accountService: AccountService,
                        private val accountRepository: AccountRepository) {

    @GetMapping
    fun getAccounts(): Iterable<Account> {
        return accountRepository.findAll()
    }

    @GetMapping("/{accountId}")
    fun getAccount(@PathVariable accountId: Long): Account? {
        return accountRepository.findOne(accountId)
    }

    @PutMapping("/{accountId}")
    fun updateAccount(@RequestBody account: Account, @PathVariable accountId: Long) {
        account.id = accountId
        accountRepository.save(account)
    }

    @PostMapping
    fun createAccount(@RequestBody account: Account): Long {
        return accountService.createAccount(account)
    }

    @DeleteMapping("/{accountId}")
    fun deleteAccount(@PathVariable accountId: Long) {
        accountRepository.delete(accountId)
    }

}
