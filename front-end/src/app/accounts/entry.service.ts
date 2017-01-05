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
      .then(response => response.json() as Entry[]);
  }

  getEntryCount(accountId: number, filter: string, page: number) {
    return this.http
      .get(this.getEntriesPath(accountId) + '/count')
      .toPromise()
      .then(response => response.json() as number);
  }

  getEntry(accountId: number, entryId: number): Promise<Entry> {
    return this.http
      .get(this.getEntriesPath(accountId) + entryId)
      .toPromise()
      .then(response => response.json() as Entry);
  }

  createEntry(accountId: number, entry: Entry): Promise<number> {
    return this.http
      .post('rest/accounts/' + accountId + '/entries/', entry)
      .toPromise()
      .then(response => response.json() as number);
  }

  updateEntry(accountId: number, entry: Entry): Promise<any> {
    return this.http
      .put('rest/accounts/' + accountId + '/entries/' + entry.id, entry)
      .toPromise();
  }

  deleteEntry(accountId: number, entryId): Promise<any> {
    return this.http
      .delete('rest/accounts/' + accountId + '/entries/' + entryId)
      .toPromise();
  }

  private getEntriesPath(accountId: number): string {
    return '/rest/accounts/' + accountId + '/entries/';
  }
}
