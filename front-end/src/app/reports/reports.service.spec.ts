import {async, inject, TestBed} from '@angular/core/testing';
import {MockBackend, MockConnection} from '@angular/http/testing';
import {HttpModule, Http, XHRBackend, Response, ResponseOptions} from '@angular/http';
import {ReportsService} from './reports.service';
import {Balance} from './balance';
import {Entry} from '../accounts/entry';
import {CashFlow} from './cash-flow';

describe('ReportsService', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        ReportsService,
        {provide: XHRBackend, useClass: MockBackend}
      ]
    });
  }));

  it('can instantiate service when inject service',
    inject([ReportsService], (service: ReportsService) => {
      expect(service instanceof ReportsService).toBeTruthy();
    })
  );

  it('can instantiate service with new', inject([Http], (http: Http) => {
    expect(http).not.toBeNull('http should be provided');
    const service = new ReportsService(http);
    expect(service instanceof ReportsService).toBe(true, 'new service should be ok');
  }));

  it('can provide the mockBackend as XHRBackend',
    inject([XHRBackend], (backend: MockBackend) => {
      expect(backend).not.toBeNull('backend should be provided');
    })
  );

  describe('when calling a service method', () => {
    let backend: MockBackend;
    let service: ReportsService;

    beforeEach(inject([Http, XHRBackend], (http: Http, be: MockBackend) => {
      backend = be;
      service = new ReportsService(http);
    }));

    it('getBalances(), no param', async(inject([], () => {
      const balances = [new Balance(), new Balance()];
      const response = new Response(new ResponseOptions({status: 200, body: balances}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getBalances(null)
        .then(res => expect(res).toEqual(balances));
    })));

    it('getBalances(), with actual date param', async(inject([], () => {
      const balances = [new Balance(), new Balance()];
      const response = new Response(new ResponseOptions({status: 200, body: balances}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getBalances('2017-01-01')
        .then(res => expect(res).toEqual(balances));
    })));

    it('getCashFlow()', async(inject([], () => {
      const cashflows = [new CashFlow(), new CashFlow()];
      const response = new Response(new ResponseOptions({status: 200, body: cashflows}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getCashFlow('2001-01-01', '2016-31-12')
        .then(res => expect(res).toEqual(cashflows));
    })));

    it('getInconsistentSplitEntries()', async(inject([], () => {
      const entries = [new Entry(), new Entry()];
      const response = new Response(new ResponseOptions({status: 200, body: entries}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getInconsistentSplitEntries()
        .then(res => expect(res).toEqual(entries));
    })));

    it('getEntriesWithoutCategory()', async(inject([], () => {
      const entries = [new Entry(), new Entry()];
      const response = new Response(new ResponseOptions({status: 200, body: entries}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getEntriesWithoutCategory()
        .then(res => expect(res).toEqual(entries));
    })));

    it('getEntriesForCategory()', async(inject([], () => {
      const balances = [new Entry(), new Entry(), new Entry()];
      const response = new Response(new ResponseOptions({status: 200, body: balances}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getEntriesForCategory(0, '2016-01-01', '2016-01-31')
        .then(res => expect(res).toEqual(balances));
    })));
  });
});
