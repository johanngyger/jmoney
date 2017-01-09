import {async, inject, TestBed} from '@angular/core/testing';
import {MockBackend, MockConnection} from '@angular/http/testing';
import {HttpModule, Http, XHRBackend, Response, ResponseOptions} from '@angular/http';
import {CategoryService} from './category.service';
import {Category} from './category';

describe('CategoryService', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        CategoryService,
        {provide: XHRBackend, useClass: MockBackend}
      ]
    });
  }));

  it('can instantiate service when inject service',
    inject([CategoryService], (service: CategoryService) => {
      expect(service instanceof CategoryService).toBeTruthy();
    })
  );

  it('can instantiate service with "new"', inject([Http], (http: Http) => {
    expect(http).not.toBeNull('http should be provided');
    let service = new CategoryService(http);
    expect(service instanceof CategoryService).toBe(true, 'new service should be ok');
  }));

  it('can provide the mockBackend as XHRBackend',
    inject([XHRBackend], (backend: MockBackend) => {
      expect(backend).not.toBeNull('backend should be provided');
    })
  );

  describe('when calling a service method', () => {
    let backend: MockBackend;
    let service: CategoryService;

    beforeEach(inject([Http, XHRBackend], (http: Http, be: MockBackend) => {
      backend = be;
      service = new CategoryService(http);
    }));

    it('getCategoryTree()', async(inject([], () => {
      let rootCat = new Category({id: 42, name: 'RooT'});
      let response = new Response(new ResponseOptions({status: 200, body: rootCat}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getCategoryTree()
        .then(cat => expect(cat).toEqual(rootCat));
    })));

    it('saveCategoryTree()', async(inject([], () => {
      let response = new Response(new ResponseOptions({status: 200}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.saveCategoryTree(new Category({id: 17, name: 'RooT2'}))
        .then(res => expect(res.status).toBe(200));
    })));

    it('getCategoryTree()', async(inject([], () => {
      let categories = [new Category({id: 48, name: '48'}), new Category({id: 21, name: '21'})];
      let response = new Response(new ResponseOptions({status: 200, body: categories}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getCategories()
        .then(cats => expect(cats).toEqual(categories));
    })));

    it('createCategory()', async(inject([], () => {
      let response = new Response(new ResponseOptions({status: 200, body: 777}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.createCategory(576)
        .then(c => expect(c).toEqual(new Category({id: 777, name: '<New category>', parentId: 576})));
    })));

    it('deleteCategory()', async(inject([], () => {
      let response = new Response(new ResponseOptions({status: 200}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.deleteCategory(9)
        .then(res => expect(res.status).toBe(200));
    })));

    it('getSplitCategory()', async(inject([], () => {
      let splitCat = new Category({id: 21, name: 'SPLIT'});
      let response = new Response(new ResponseOptions({status: 200, body: splitCat}));
      backend.connections.subscribe((c: MockConnection) => c.mockRespond(response));
      service.getSplitCategory()
        .then(c => expect(c).toEqual(splitCat));
    })));
  });
});
