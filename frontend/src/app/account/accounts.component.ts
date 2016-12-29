import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {AccountService} from "./account.service";
import {Account} from "./account";

@Component({
  selector: 'jmoney-accounts',
  templateUrl: './accounts.component.html'
})
export class AccountsComponent implements OnInit {
  accounts: Account[];

  constructor(private accountService: AccountService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.accountService.getAccounts()
      .then(accounts => this.accounts = accounts)
      .catch(error => console.log(error));
  }
}
