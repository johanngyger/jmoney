import {fakeAsync, tick, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement, NO_ERRORS_SCHEMA} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {ReportsService} from './reports.service';
import {CashFlowComponent} from './cash-flow.component';
import {CashFlow} from './cash-flow';
import * as moment from 'moment';
import {Entry} from '../accounts/entry';

describe('CashFlowComponent', () => {
  let comp: CashFlowComponent;
  let fixture: ComponentFixture<CashFlowComponent>;
  let de: DebugElement;
  const cashFlow = [
    new CashFlow({categoryId: 0, categoryName: 'Cat 1', income: 10000, expense: 20000, difference: 10000}),
    new CashFlow({categoryId: 0, categoryName: 'Cat 2', income: 10000, expense: 20000, difference: 10000}),
    new CashFlow({categoryId: 0, categoryName: 'Cat 3', income: 10000, expense: 20000, difference: 10000}),
    new CashFlow({categoryId: 0, categoryName: 'Cat 4', income: 10000, expense: 20000, difference: 10000}),
    new CashFlow({categoryId: 0, categoryName: 'Total', income: 10000, expense: 20000, difference: 10000, total: true}),
    new CashFlow(),
  ];
  const entries = [
    new Entry({description: 'Entry1'}),
    new Entry({description: 'Entry2'}),
    new Entry({description: 'Entry3'}),
    new Entry({description: 'Entry4'}),
    new Entry({description: 'Entry5'})
  ];
  let success: boolean;

  class FakeReportsService {
    getEntriesForCategory(categoryId: number, fromDate: string, toDate: string): Promise<Entry[]> {
      return success ? Promise.resolve(entries) : Promise.reject(success);
    }

    getCashFlow(fromDate: string, toDate: string): Promise<CashFlow[]> {
      return success ? Promise.resolve(cashFlow) : Promise.reject(success);
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [CashFlowComponent],
      providers: [{provide: ReportsService, useClass: FakeReportsService}],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(CashFlowComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    success = true;
  });

  const handleChanges = function () {
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
  };

  it('should fetchCashFlow()', fakeAsync(() => {
    comp.fetchCashFlow();
    handleChanges();
    const cfItems = de.queryAll(By.css('tr'));
    expect(cfItems.length).toBe(7);
    expect(cfItems[1].nativeElement.textContent).toContain('Cat 1');
    expect(cfItems[2].nativeElement.textContent).toContain('Cat 2');
    expect(cfItems[3].nativeElement.textContent).toContain('Cat 3');
    expect(cfItems[4].nativeElement.textContent).toContain('Cat 4');
    expect(cfItems[5].nativeElement.textContent).toContain('Total');
  }));

  it('should show an an error message when fetchCashFlow() fails', fakeAsync(() => {
    success = false;
    comp.fetchCashFlow();
    handleChanges();
    expect(de.query(By.css('.alert-error'))).toBeTruthy();
  }));

  it('should show the entries when clicking a cash flow item', fakeAsync(() => {
    comp.getEntriesForCategory(0);
    handleChanges();
    expect(comp.entries).toBeTruthy();
    expect(comp.entries.length).toBe(5);
  }));

  it('should show the entries when clicking a cash flow item', fakeAsync(() => {
    success = false;
    comp.getEntriesForCategory(0);
    handleChanges();
    expect(de.query(By.css('.alert-error'))).toBeTruthy();
  }));

  it('should select the correct period (thisMonth)', () => {
    comp.period = 'thisMonth';
    comp.onPeriodChange();
    const year = moment().year();
    expect(comp.fromDate).toBe(year + moment().startOf('month').format('-MM-DD'));
    expect(comp.toDate).toBe(year + moment().endOf('month').format('-MM-DD'));
  });

  it('should select the correct period (thisYear)', () => {
    comp.period = 'thisYear';
    comp.onPeriodChange();
    const thisYear = moment().year();
    expect(comp.fromDate).toBe(thisYear + '-01-01');
    expect(comp.toDate).toBe(thisYear + '-12-31');
  });

  it('should select the correct period (lastMonth)', () => {
    comp.period = 'lastMonth';
    comp.onPeriodChange();
    expect(comp.fromDate).toBe(moment().subtract(1, 'month').startOf('month').format('YYYY-MM-DD'));
    expect(comp.toDate).toBe(moment().subtract(1, 'month').endOf('month').format('YYYY-MM-DD'));
  });

  it('should select the correct period (lastYear)', () => {
    comp.period = 'lastYear';
    comp.onPeriodChange();
    const lastYear = moment().year() - 1;
    expect(comp.fromDate).toBe(lastYear + '-01-01');
    expect(comp.toDate).toBe(lastYear + '-12-31');
  });
});
