import {fakeAsync, tick, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement, NO_ERRORS_SCHEMA} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {ActivatedRouteStub} from '../testing/route-stubs';
import {EntriesComponent} from './entries.component';
import {EntryService} from './entry.service';
import {Entry} from './entry';

describe('EntriesComponent', () => {
  let activatedRoute: ActivatedRouteStub;
  let comp: EntriesComponent;
  let fixture: ComponentFixture<EntriesComponent>;
  let de: DebugElement;
  let success: boolean;
  const entries = [
    new Entry({description: 'Entry1'}),
    new Entry({description: 'Entry2'}),
    new Entry({description: 'Entry3'}),
    new Entry({description: 'Entry4'})
  ];

  class FakeEntryService {
    getEntries(accountId: number, filter: string, page: number): Promise<Entry[]> {
      return success ? Promise.resolve(entries) : Promise.reject(success);
    }

    getEntryCount(accountId: number, filter: string, page: number): Promise<number> {
      return success ? Promise.resolve(186) : Promise.reject(success);
    }
  }

  beforeEach(() => {
    activatedRoute = new ActivatedRouteStub();
    activatedRoute.testParams = {accountId: 99999};
    activatedRoute.testQueryParams = {};

    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [EntriesComponent],
      providers: [
        {provide: ActivatedRoute, useValue: activatedRoute},
        {provide: EntryService, useClass: FakeEntryService}
      ],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(EntriesComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
  });

  const handleChanges = function () {
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
  };

  it('can navigate through entries (first page)', fakeAsync(() => {
    success = true;
    handleChanges();
    expect(comp.prevPage()).toBe(1);
    expect(comp.nextPage()).toBe(2);
  }));

  it('can navigate through entries (middle page)', fakeAsync(() => {
    success = true;
    activatedRoute.testQueryParams = {page: 10};
    handleChanges();
    expect(comp.prevPage()).toBe(9);
    expect(comp.nextPage()).toBe(11);
  }));

  it('can navigate through entries (last page)', fakeAsync(() => {
    success = true;
    activatedRoute.testQueryParams = {page: 19};
    handleChanges();
    expect(comp.prevPage()).toBe(18);
    expect(comp.nextPage()).toBe(19);
  }));

});

