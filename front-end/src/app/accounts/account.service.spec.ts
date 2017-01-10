import {async, inject, TestBed} from '@angular/core/testing';
import {MockBackend, MockConnection} from '@angular/http/testing';
import {HttpModule, Http, XHRBackend, Response, ResponseOptions} from '@angular/http';
import {AccountService} from './account.service';
import {Account} from './account';

describe('AccountService', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        AccountService,
        {provide: XHRBackend, useClass: MockBackend}
      ]
    });
  }));

  it('can instantiate service when inject service',
    inject([AccountService], (service: AccountService) => {
      expect(service instanceof AccountService).toBeTruthy();
    })
  );

  it('can instantiate service with new', inject([Http], (http: Http) => {
    expect(http).not.toBeNull('http should be provided');
    let service = new AccountService(http);
    expect(service instanceof AccountService).toBe(true, 'new service should be ok');
  }));

  it('can provide the mockBackend as XHRBackend',
    inject([XHRBackend], (backend: MockBackend) => {
      expect(backend).not.toBeNull('backend should be provided');
    })
  );

  describe('when calling a service method', () => {
    let backend: MockBackend;
    let service: AccountService;

    beforeEach(inject([Http, XHRBackend], (http: Http, be: MockBackend) => {
      backend = be;
      service = new AccountService(http);
    }));

    it('getAccounts(), no param', async(inject([], () => {
      let accounts = [new Account(), new Account()];
      let response = new Response(new ResponseOptions({status: 200, body: accounts}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getAccounts()
        .then(res => expect(res).toEqual(accounts));
    })));

    it('getAccount()', async(inject([], () => {
      let account = new Account();
      let response = new Response(new ResponseOptions({status: 200, body: account}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getAccount(0)
        .then(res => expect(res).toEqual(account));
    })));

    it('createAccount()', async(inject([], () => {
      let account = new Account({name: 'Account A'});
      let response = new Response(new ResponseOptions({status: 200, body: account}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.createAccount(account)
        .then(res => expect(res).toEqual(account));
    })));

    it('updateAccount()', async(inject([], () => {
      let account = new Account();
      let response = new Response(new ResponseOptions({status: 200, body: account}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.updateAccount(account)
        .then(res => expect(res.status).toBe(200));
    })));

    it('deleteAccount()', async(inject([], () => {
      let response = new Response(new ResponseOptions({status: 200}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.deleteAccount(0)
        .then(res => expect(res.status).toBe(200));
    })));
  });
});
