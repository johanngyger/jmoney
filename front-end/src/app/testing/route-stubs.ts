import {BehaviorSubject} from 'rxjs/BehaviorSubject';

export class ActivatedRouteStub {
  private paramsSubject = new BehaviorSubject(this.testParams);
  params = this.paramsSubject.asObservable();

  private _testParams: {};
  get testParams() {
    return this._testParams;
  }

  set testParams(params: {}) {
    this._testParams = params;
    this.paramsSubject.next(params);
  }

  private queryParamsSubject = new BehaviorSubject(this.testQueryParams);
  queryParams = this.queryParamsSubject.asObservable();

  private _testQueryParams: {};
  get testQueryParams() {
    return this._testQueryParams;
  }

  set testQueryParams(params: {}) {
    this._testQueryParams = params;
    this.queryParamsSubject.next(params);
  }
}
