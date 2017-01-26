import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import 'rxjs/add/operator/toPromise';

@Injectable()
export class OptionsService {

  constructor(private http: Http) {
  }

  init(): Promise<any> {
    return this.http.put('/api/options/init', {timeout: 5000}).toPromise();
  }

}
