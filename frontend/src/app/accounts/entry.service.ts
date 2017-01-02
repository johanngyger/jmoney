import {Injectable} from "@angular/core";
import {URLSearchParams, Http} from "@angular/http";
import {Entry} from "./entry";

@Injectable()
export class EntryService {
  constructor(private http: Http) {
  }

  getEntries(accountId: number, filter: string, page: number): Promise<Entry[]> {
    let params = new URLSearchParams();
    if (filter) params.set('filter', filter);
    if (page) params.set('page', '' + page);

    return this.http
      .get(this.getEntriesPath(accountId), {search: params})
      .toPromise()
      .then(response => response.json() as Entry[])
      .catch(this.handleError);
  }

  getEntryCount(accountId: number, filter: string, page: number) {
    return this.http
      .get(this.getEntriesPath(accountId) + '/count')
      .toPromise()
      .then(response => response.json() as number);
  }

  private getEntriesPath(accountId: number): string {
    return '/rest/accounts/' + accountId + '/entries/';
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
