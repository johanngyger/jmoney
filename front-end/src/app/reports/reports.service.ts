import {Injectable} from '@angular/core';
import {URLSearchParams, Http} from '@angular/http';
import 'rxjs/add/operator/toPromise';
import {Balance} from './balance';
import {CashFlow} from './cash-flow';
import {Entry} from '../accounts/entry';

@Injectable()
export class ReportsService {
  accountsPath = '/rest/accounts';

  constructor(private http: Http) {
  }

  getBalances(date: string): Promise<Balance[]> {
    let params = new URLSearchParams();
    if (date) {
      params.set('date', date);
    }

    return this.http
      .get('/rest/reports/balances', {search: params})
      .toPromise()
      .then(response => response.json() as Balance[]);
  }

  getCashFlow(fromDate: string, toDate: string): Promise<CashFlow[]> {
    let params = new URLSearchParams();
    params.set('fromDate', fromDate);
    params.set('toDate', toDate);

    return this.http
      .get('/rest/reports/cash-flows', {search: params})
      .toPromise()
      .then(response => response.json() as CashFlow[]);
  }

  getInconsistentSplitEntries(): Promise<Entry[]> {
    return this.http
      .get('rest/reports/consitency/inconsistent-split-entries')
      .toPromise()
      .then(response => response.json() as Balance[]);
  }

  getEntriesWithoutCategory(): Promise<Entry[]> {
    return this.http
      .get('rest/reports/consitency/entries-without-category')
      .toPromise()
      .then(response => response.json() as Balance[]);
  }
}
