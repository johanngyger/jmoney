import {fakeAsync, tick, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {ReportsService} from './reports.service';
import {CashFlowComponent} from './cash-flow.component';
import {CashFlow} from './cash-flow';
import * as moment from 'moment';

describe('CashFlowComponent', () => {
  let comp: CashFlowComponent;
  let fixture: ComponentFixture<CashFlowComponent>;
  let de: DebugElement;
  let cashFlow = [
    new CashFlow({categoryId: 0, categoryName: 'Cat 1', income: 10000, expense: 20000, difference: 10000}),
    new CashFlow({categoryId: 0, categoryName: 'Cat 2', income: 10000, expense: 20000, difference: 10000}),
    new CashFlow({categoryId: 0, categoryName: 'Cat 3', income: 10000, expense: 20000, difference: 10000}),
    new CashFlow({categoryId: 0, categoryName: 'Cat 4', income: 10000, expense: 20000, difference: 10000}),
    new CashFlow({categoryId: 0, categoryName: 'Total', income: 10000, expense: 20000, difference: 10000, total: true}),
    new CashFlow(),
  ];
  let success: boolean;

  class FakeReportsService {
    getCashFlow(fromDate: string, toDate: string): Promise<CashFlow[]> {
      return success ? Promise.resolve(cashFlow) : Promise.reject(success);
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [CashFlowComponent],
      providers: [{provide: ReportsService, useClass: FakeReportsService}]
    });
    fixture = TestBed.createComponent(CashFlowComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
  });

  let handleChanges = function () {
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
  };

  it('should fetchCashFlow()', fakeAsync(() => {
    success = true;
    comp.fetchCashFlow();
    handleChanges();
    let entries = de.queryAll(By.css('tr'));
    expect(entries.length).toBe(7);
    expect(entries[1].nativeElement.textContent).toContain('Cat 1');
    expect(entries[2].nativeElement.textContent).toContain('Cat 2');
    expect(entries[3].nativeElement.textContent).toContain('Cat 3');
    expect(entries[4].nativeElement.textContent).toContain('Cat 4');
    expect(entries[5].nativeElement.textContent).toContain('Total');
  }));

  it('should show an an error message when fetchCashFlow() fails', fakeAsync(() => {
    success = false;
    comp.fetchCashFlow();
    handleChanges();
    expect(de.query(By.css('.alert-error'))).toBeTruthy();
  }));

  it('should select the correct period (thisMonth)', () => {
    comp.period = 'thisMonth';
    comp.onPeriodChange();
    let year = moment().year();
    expect(comp.fromDate).toBe(year + moment().startOf('month').format('-MM-DD'));
    expect(comp.toDate).toBe(year + moment().endOf('month').format('-MM-DD'));
  });

  it('should select the correct period (thisYear)', () => {
    comp.period = 'thisYear';
    comp.onPeriodChange();
    let thisYear = moment().year();
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
    let lastYear = moment().year() - 1;
    expect(comp.fromDate).toBe(lastYear + '-01-01');
    expect(comp.toDate).toBe(lastYear + '-12-31');
  });
});
