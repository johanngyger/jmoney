import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import 'rxjs/add/operator/toPromise';
import {Subject} from 'rxjs/Subject';
import {Account} from './account';

@Injectable()
export class AccountService {
  accountsPath = '/api/accounts';
  accountChange = new Subject<number>();

  constructor(private http: Http) {
  }

  getAccounts(): Promise<Account[]> {
    return this.http
      .get(this.accountsPath)
      .toPromise()
      .then(response => response.json() as Account[]);
  }

  getAccount(id: number): Promise<Account> {
    const url = `${this.accountsPath}/${id}`;
    return this.http.get(url)
      .toPromise()
      .then(response => response.json() as Account);
  }

  createAccount(account: Account): Promise<number> {
    return this.http.post(this.accountsPath, account)
      .toPromise()
      .then(response => response.json() as number)
      .then(accountId => {
        this.accountChange.next(accountId);
        return accountId;
      });
  }

  updateAccount(account: Account): Promise<any> {
    return this.http.put(`${this.accountsPath}/${account.id}`, account)
      .toPromise()
      .then(res => {
        this.accountChange.next(account.id);
        return res;
      });
  }

  deleteAccount(accountId): Promise<any> {
    return this.http.delete(`${this.accountsPath}/${accountId}`)
      .toPromise()
      .then(res => {
        this.accountChange.next(accountId);
        return res;
      });
  }

}
