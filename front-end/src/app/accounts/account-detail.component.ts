import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AccountService} from './account.service';
import {Account} from './account';

@Component({
  templateUrl: './account-detail.component.html'
})
export class AccountDetailComponent implements OnInit {
  account: Account;

  constructor(private accountService: AccountService, private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    this.route.params
      .switchMap(params => {
        const id = params['accountId'];
        if (id) {
          return this.accountService.getAccount(+id);
        } else {
          return Promise.resolve(new Account());
        }
      })
      .subscribe(account => this.account = account);
  }

  save(): void {
    if (this.account.id) {
      this.accountService.updateAccount(this.account)
        .then(() => this.router.navigate(['/accounts', this.account.id, 'entries']));
    } else {
      this.accountService.createAccount(this.account)
        .then(accountId => this.router.navigate(['/accounts', accountId, 'entries']));
    }
  }

  delete(): void {
    if (confirm('Really delete this account?')) {
      this.accountService.deleteAccount(this.account.id)
        .then(() => this.router.navigate(['/accounts']));
    }
  }
}
