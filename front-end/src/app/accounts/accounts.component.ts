import {Component, OnInit, OnDestroy} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {AccountService} from './account.service';
import {Account} from './account';

@Component({
  templateUrl: './accounts.component.html'
})
export class AccountsComponent implements OnInit, OnDestroy {
  accounts: Account[];
  accountId: number;
  accountChanges: Subscription;
  error: boolean;

  constructor(private accountService: AccountService) {
    this.accountChanges = accountService.accountChange
      .subscribe(accountId => {
        this.accountId = accountId;
        this.getAccounts();
      });
  }

  ngOnInit(): void {
    this.getAccounts();
  }

  getAccounts() {
    this.accountService.getAccounts()
      .then(accounts => this.accounts = accounts)
      .catch(() => this.error = true);
  }

  ngOnDestroy(): void {
    this.accountChanges.unsubscribe();
  }

  onSelected(accountId: number) {
    this.accountId = accountId;
  }

}
