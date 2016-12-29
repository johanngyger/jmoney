import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Params} from "@angular/router";
import {AccountService} from "./account.service";
import {Account} from "./account";

@Component({
  templateUrl: './accounts.component.html'
})
export class AccountsComponent implements OnInit {
  accounts: Account[];
  selectedAccount: Account;

  constructor(private accountService: AccountService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.accountService.getAccounts()
      .then(accounts => this.accounts = accounts)
      .catch(error => console.log(error));

    this.route.params
      .switchMap((params: Params) => this.accountService.getAccount(+params['accountId']))
      .subscribe(selectedAccount => this.selectedAccount = selectedAccount);
  }
}
