import { Injectable } from '@angular/core';
import { Headers, Http, Response } from '@angular/http';
import 'rxjs/add/operator/toPromise';
import { Account } from './account';

@Injectable()
export class AccountService {
  private accountsUrl = '/rest/accounts/';  // URL to web api

  constructor(private http: Http) { }

  getAccounts(): Promise<Account[]> {
    return this.http
      .get(this.accountsUrl)
      .toPromise()
      .then(response => response.json() as Account[])
      .catch(this.handleError);
  }

  getAccount(id: number): Promise<Account> {
    const url = `${this.accountsUrl}/${id}`;
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
