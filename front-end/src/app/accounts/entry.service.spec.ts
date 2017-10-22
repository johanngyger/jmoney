import {async, inject, TestBed} from '@angular/core/testing';
import {MockBackend, MockConnection} from '@angular/http/testing';
import {HttpModule, Http, XHRBackend, Response, ResponseOptions} from '@angular/http';
import {EntryService} from './entry.service';
import {Entry} from './entry';

describe('EntryService', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        EntryService,
        {provide: XHRBackend, useClass: MockBackend}
      ]
    });
  }));

  it('can instantiate service when inject service',
    inject([EntryService], (service: EntryService) => {
      expect(service instanceof EntryService).toBeTruthy();
    })
  );

  it('can instantiate service with new', inject([Http], (http: Http) => {
    expect(http).not.toBeNull('http should be provided');
    const service = new EntryService(http);
    expect(service instanceof EntryService).toBe(true, 'new service should be ok');
  }));

  it('can provide the mockBackend as XHRBackend',
    inject([XHRBackend], (backend: MockBackend) => {
      expect(backend).not.toBeNull('backend should be provided');
    })
  );

  describe('when calling a service method', () => {
    let backend: MockBackend;
    let service: EntryService;

    beforeEach(inject([Http, XHRBackend], (http: Http, be: MockBackend) => {
      backend = be;
      service = new EntryService(http);
    }));

    it('getEntries(), no param', async(inject([], () => {
      const entries = [new Entry(), new Entry()];
      const response = new Response(new ResponseOptions({status: 200, body: entries}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getEntries(0, null, null)
        .then(res => expect(res).toEqual(entries));
    })));

    it('getEntries(), with params', async(inject([], () => {
      const entries = [new Entry({id: 1, status: null, creation: 0})];
      const response = new Response(new ResponseOptions({status: 200, body: entries}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getEntries(0, 'foo', 1)
        .then(res => expect(res).toEqual(entries));
    })));

    it('getEntryCount()', async(inject([], () => {
      const response = new Response(new ResponseOptions({status: 200, body: 57}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getEntryCount(0, null, null)
        .then(res => expect(res).toBe(57));
    })));

    it('getEntryCount(), with params', async(inject([], () => {
      const response = new Response(new ResponseOptions({status: 200, body: 57}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getEntryCount(0, 'bar', 2)
        .then(res => expect(res).toBe(57));
    })));

    it('getEntry()', async(inject([], () => {
      const entry = new Entry({id: 17});
      const response = new Response(new ResponseOptions({status: 200, body: entry}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getEntry(24, 17)
        .then(res => expect(res).toEqual(entry));
    })));

    it('createEntry()', async(inject([], () => {
      const entry = new Entry();
      const response = new Response(new ResponseOptions({status: 200, body: 17}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.createEntry(24, entry)
        .then(res => expect(res).toEqual(17));
    })));

    it('updateEntry()', async(inject([], () => {
      const entry = new Entry({id: 27, description: 'Entry27'});
      const response = new Response(new ResponseOptions({status: 200, body: entry}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.updateEntry(2, entry)
        .then(res => expect(res.status).toBe(200));
    })));

    it('deleteEntry()', async(inject([], () => {
      const response = new Response(new ResponseOptions({status: 200, body: 22}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.deleteEntry(9, 243)
        .then(res => expect(res.status).toBe(200));
    })));
  });
});
