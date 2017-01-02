import { Injectable } from '@angular/core';
import { Headers, Http, Response } from '@angular/http';
import 'rxjs/add/operator/toPromise';
import { Account } from './account';

@Injectable()
export class AccountService {
  private accountsPath = '/rest/accounts';

  constructor(private http: Http) { }

  getAccounts(): Promise<Account[]> {
    return this.http
      .get(this.accountsPath)
      .toPromise()
      .then(response => response.json() as Account[])
      .catch(this.handleError);
  }

  getAccount(id: number): Promise<Account> {
    const url = `${this.accountsPath}/${id}`;
    return this.http.get(url)
      .toPromise()
      .then(response => response.json() as Account)
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
