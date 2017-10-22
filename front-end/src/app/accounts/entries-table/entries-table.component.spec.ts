import {ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement, NO_ERRORS_SCHEMA} from '@angular/core';
import {By} from '@angular/platform-browser';
import {EntriesTableComponent} from './entries-table.component';
import {Entry} from '../entry';

describe('EntriesTableComponent', () => {
  let comp: EntriesTableComponent;
  let fixture: ComponentFixture<EntriesTableComponent>;
  let de: DebugElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EntriesTableComponent],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(EntriesTableComponent);
    comp = fixture.componentInstance;
    comp.entries = [
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
    de = fixture.debugElement;
    fixture.detectChanges();
  });

  it('can show a table with three entries', () => {
    const entryTable = de.query(By.css('table.entryTable'));
    expect(entryTable.nativeElement).toBeTruthy();
    const rows = entryTable.queryAll(By.css('tr'));

    expect(rows.length).toBe(4);
    expect(rows[0].nativeElement.textContent).toContain('Description');

    expect(rows[1].nativeElement.textContent).toContain('Desc1');
    expect(rows[1].nativeElement.textContent).toContain('10.00');

    expect(rows[2].nativeElement.textContent).toContain('Desc2');
    expect(rows[2].nativeElement.textContent).toContain('17.00');

    expect(rows[3].nativeElement.textContent).toContain('Desc3');
    expect(rows[3].nativeElement.textContent).toContain('123.45');
  });

});
