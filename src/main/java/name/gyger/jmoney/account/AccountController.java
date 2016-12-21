/*
 * Copyright 2012 Johann Gyger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
