package name.gyger.jmoney.account

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/accounts")
class AccountController(private val accountService: AccountService) {

    @GetMapping
    fun getAccounts(): Collection<Account> {
        return accountService.getAccounts()
    }

    @GetMapping("/{accountId}")
    fun getAccount(@PathVariable accountId: Long): Account? {
        return accountService.getAccount(accountId)
    }

    @PutMapping("/{accountId}")
    fun updateAccount(@RequestBody account: Account, @PathVariable accountId: Long) {
        account.id = accountId
        accountService.updateAccount(account)
    }

    @PostMapping
    fun createAccount(@RequestBody account: Account): Long {
        return accountService.createAccount(account)
    }

    @DeleteMapping("/{accountId}")
    fun deleteAccount(@PathVariable accountId: Long) {
        accountService.deleteAccount(accountId)
    }

}
