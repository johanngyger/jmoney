import {fakeAsync, tick, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {BalancesComponent} from './balances.component';
import {ReportsService} from './reports.service';
import {Balance} from './balance';

describe('BalancesComponent', () => {
  let comp: BalancesComponent;
  let fixture: ComponentFixture<BalancesComponent>;
  let de: DebugElement;
  const balances = [
    new Balance({accountName: 'Account A', balance: 12345, total: false}),
    new Balance({accountName: 'Account B', balance: -10000, total: false}),
    new Balance({accountName: 'Account C', balance: 1528000, total: false}),
    new Balance({accountName: 'Total', balance: 1529345, total: false}),
  ];
  let success: boolean;

  class FakeReportsService {
    getBalances(date: string): Promise<Balance[]> {
      return success ? Promise.resolve(balances) : Promise.reject(success);
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [BalancesComponent],
      providers: [{provide: ReportsService, useClass: FakeReportsService}]
    });
    fixture = TestBed.createComponent(BalancesComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
  });

  const handleChanges = function () {
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
  };

  it('should fetchBalances()', fakeAsync(() => {
    success = true;
    comp.fetchBalances();
    handleChanges();
    const entries = de.queryAll(By.css('div.report-entry'));
    expect(entries.length).toBe(4);
    expect(entries[0].nativeElement.textContent).toContain('Account A');
    expect(entries[1].nativeElement.textContent).toContain('Account B');
    expect(entries[2].nativeElement.textContent).toContain('Account C');
    expect(entries[3].nativeElement.textContent).toContain('Total');
  }));

  it('should show an an error message when fetchBalances() fails', fakeAsync(() => {
    success = false;
    comp.fetchBalances();
    handleChanges();
    expect(de.query(By.css('.alert-error'))).toBeTruthy();
  }));
});
