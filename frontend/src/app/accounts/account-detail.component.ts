import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {AccountService} from "./account.service";
import {Account} from "./account";

@Component({
  selector: 'jmoney-account-detail',
  templateUrl: './account-detail.component.html'
})
export class AccountDetailComponent implements OnInit {
  account: Account;

  constructor(private accountService: AccountService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.params
      .switchMap(params => this.accountService.getAccount(+params['accountId']))
      .subscribe(account => this.account = account);
  }

}
