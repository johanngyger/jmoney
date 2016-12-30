import {Injectable} from "@angular/core";
import {DatePipe} from "@angular/common";
import {URLSearchParams, Http} from "@angular/http";
import "rxjs/add/operator/toPromise";
import {Balance} from "./balance";
import {CashFlow} from "./cash-flow";
import {Entry} from "../account/entry";

@Injectable()
export class ReportsService {
  private accountsPath = '/rest/accounts';

  constructor(private http: Http, private datePipe: DatePipe) {
  }

  getBalances(date: string): Promise<Balance[]> {
    let params = new URLSearchParams();
    if (date) {
      params.set('date', date);
    }

    return this.http
      .get('/rest/reports/balances', {search: params})
      .toPromise()
      .then(response => response.json() as Balance[])
      .catch(this.handleError);
  }

  getCashFlow(fromDate: string, toDate: string): Promise<CashFlow[]> {
    let params = new URLSearchParams();
    params.set('fromDate', fromDate);
    params.set('toDate', toDate);

    return this.http
      .get('/rest/reports/cash-flows', {search: params})
      .toPromise()
      .then(response => response.json() as CashFlow[])
      .catch(this.handleError);
  }

  getInconsistentSplitEntries(): Promise<Entry[]> {
    return this.http
      .get('rest/reports/consitency/inconsistent-split-entries')
      .toPromise()
      .then(response => response.json() as Balance[])
      .catch(this.handleError);
  }

  getEntriesWithoutCategory(): Promise<Entry[]> {
    return this.http
      .get('rest/reports/consitency/entries-without-category')
      .toPromise()
      .then(response => response.json() as Balance[])
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
