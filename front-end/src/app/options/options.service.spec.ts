import {async, inject, TestBed} from '@angular/core/testing';
import {MockBackend, MockConnection} from '@angular/http/testing';
import {HttpModule, Http, XHRBackend, Response, ResponseOptions} from '@angular/http';
import {OptionsService} from './options.service';

describe('OptionsService', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        OptionsService,
        {provide: XHRBackend, useClass: MockBackend}
      ]
    });
  }));

  it('can instantiate service when inject service',
    inject([OptionsService], (service: OptionsService) => {
      expect(service instanceof OptionsService).toBeTruthy();
    })
  );

  it('can instantiate service with new', inject([Http], (http: Http) => {
    expect(http).not.toBeNull('http should be provided');
    const service = new OptionsService(http);
    expect(service instanceof OptionsService).toBe(true, 'new service should be ok');
  }));

  it('can provide the mockBackend as XHRBackend',
    inject([XHRBackend], (backend: MockBackend) => {
      expect(backend).not.toBeNull('backend should be provided');
    })
  );

  describe('when init()', () => {
    let backend: MockBackend;
    let service: OptionsService;
    let response: Response;

    beforeEach(inject([Http, XHRBackend], (http: Http, be: MockBackend) => {
      backend = be;
      service = new OptionsService(http);
      const options = new ResponseOptions({status: 200});
      response = new Response(options);
    }));

    it('should have expected expected response status', async(inject([], () => {
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.init()
        .then(res => expect(res.status).toBe(200));
    })));
  });

});
