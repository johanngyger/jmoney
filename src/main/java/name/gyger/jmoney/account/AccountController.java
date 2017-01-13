package name.gyger.jmoney.account;

import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/rest/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Collection<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @RequestMapping(path = "/{accountId}", method = RequestMethod.GET)
    public Account getAccount(@PathVariable long accountId) {
        return accountService.getAccount(accountId);
    }

    @RequestMapping(path = "/{accountId}", method = RequestMethod.PUT)
    public void updateAccount(@RequestBody Account account, @PathVariable long accountId) {
        account.setId(accountId);
        accountService.updateAccount(account);
    }

    @RequestMapping(method = RequestMethod.POST)
    public long createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    @RequestMapping(path = "/{accountId}", method = RequestMethod.DELETE)
    public void deleteAccount(@PathVariable long accountId) {
        accountService.deleteAccount(accountId);
    }

}
