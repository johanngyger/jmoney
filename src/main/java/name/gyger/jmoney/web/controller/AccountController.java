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

package name.gyger.jmoney.web.controller;

import name.gyger.jmoney.dto.AccountDetailsDto;
import name.gyger.jmoney.dto.AccountDto;
import name.gyger.jmoney.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
@RequestMapping("/rest/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Collection<AccountDto> getAccounts() {
        return accountService.getAccounts();
    }

    @RequestMapping(path = "/{accountId}", method = RequestMethod.GET)
    @ResponseBody
    public AccountDetailsDto getAccountDetails(@PathVariable long accountId) {
        return accountService.getAccountDetails(accountId);
    }

    @RequestMapping(path = "/{accountId}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateAccount(@RequestBody AccountDetailsDto account, @PathVariable long accountId) {
        accountService.updateAccount(account);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public long createAccount(@RequestBody AccountDetailsDto account) {
        return accountService.createAccount(account);
    }

    @RequestMapping(path = "/{accountId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deletePerson(@PathVariable long accountId) {
        accountService.deleteAccount(accountId);
    }

}
