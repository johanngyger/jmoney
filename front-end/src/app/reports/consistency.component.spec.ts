import {fakeAsync, tick, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement, NO_ERRORS_SCHEMA} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {ReportsService} from './reports.service';
import {ConsistencyComponent} from './consistency.component';
import {EntriesTableComponent} from '../accounts/entries-table/entries-table.component';
import {Entry} from '../accounts/entry';


describe('ConsistencyComponent', () => {
  let comp: ConsistencyComponent;
  let fixture: ComponentFixture<ConsistencyComponent>;
  let de: DebugElement;
  const entries = [
    new Entry({
      date: 1483228800000,
      valuta: 1483833600000,
      description: 'Desc1',
      categoryName: 'Cat1',
      amount: 1000,
      balance: 52300
    }),
    new Entry({
      date: 1483228800000,
      valuta: 1483833600000,
      description: 'Desc2',
      categoryName: 'Cat2',
      amount: -1700,
      balance: 12665,
      status: 'RECONCILING'
    }),
    new Entry({
      date: 1483228800000,
      valuta: 1483833600000,
      description: 'Desc3',
      categoryName: 'Cat3',
      amount: 12345,
      balance: 12345,
      status: 'CLEARED'
    }),
  ];
  let success: boolean;

  class FakeReportsService {
    getInconsistentSplitEntries(): Promise<Entry[]> {
      return success ? Promise.resolve(entries) : Promise.reject(success);
    }

    getEntriesWithoutCategory(): Promise<Entry[]> {
      return success ? Promise.resolve(entries) : Promise.reject(success);
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [ConsistencyComponent, EntriesTableComponent],
      providers: [{provide: ReportsService, useClass: FakeReportsService}],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(ConsistencyComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
  });

  const handleChanges = function () {
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
  };

  it('should show inconsistent split entries and entries without category', fakeAsync(() => {
    success = true;
    handleChanges();
    const entryElems = de.queryAll(By.css('tr'));
    expect(entryElems.length).toBe(8);
  }));

  it('should show an an error message when fetching of entries fails', fakeAsync(() => {
    success = false;
    handleChanges();
    expect(de.query(By.css('.alert-error'))).toBeTruthy();
  }));
});
