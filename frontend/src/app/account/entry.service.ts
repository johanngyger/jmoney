import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {Entry} from "./entry";

@Injectable()
export class EntryService {
  constructor(private http: Http) {
  }

  getEntries(accountId: number): Promise<Entry[]> {
    return this.http
      .get(this.getEntriesPath(accountId))
      .toPromise()
      .then(response => response.json() as Entry[])
      .catch(this.handleError);
  }

  private getEntriesPath(accountId: number): string {
    return '/rest/accounts/' + accountId + '/entries/';
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error);
    return Promise.reject(error.message || error);
  }
}
