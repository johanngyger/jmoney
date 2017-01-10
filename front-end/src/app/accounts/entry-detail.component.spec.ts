import {fakeAsync, tick, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement, NO_ERRORS_SCHEMA} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {ActivatedRoute, Router} from '@angular/router';
import {ActivatedRouteStub} from '../testing/route-stubs';
import {EntryService} from './entry.service';
import {EntryDetailComponent} from './entry-detail.component';
import {Entry} from '../accounts/entry';
import {CategoryService} from '../categories/category.service';
import {Category} from '../categories/category';

describe('EntryDetailComponent', () => {
  let activatedRoute = new ActivatedRouteStub();
  let comp: EntryDetailComponent;
  let fixture: ComponentFixture<EntryDetailComponent>;
  let de: DebugElement;
  let entry: Entry;
  let success: boolean;

  class FakeEntryService {
    getEntry(accountId: number, entryId: number): Promise<Entry> {
      return Promise.resolve(entry);
    }

    createEntry(accountId: number, e: Entry): Promise<number> {
      return Promise.resolve(88);
    }

    updateEntry(accountId: number, e: Entry): Promise<any> {
      return Promise.resolve(true);
    }

    deleteEntry(accountId: number, entryId): Promise<any> {
      return Promise.resolve(true);
    }
  }

  class FakeCategoryService {
    getCategories(): Promise<Category[]> {
      return Promise.resolve([
        new Category({id: 1, name: 'Cat1', nameIndented: '[Cat1]'}),
        new Category({id: 2, name: 'Cat2', nameIndented: '[Cat2]'}),
        new Category({id: 3, name: 'Cat3', nameIndented: '[Cat3]'})
      ]);
    }

    getSplitCategory(): Promise<Category> {
      return Promise.resolve(new Category({id: 444, name: 'SPLIT'}));
    }
  }

  class RouterStub {
    navigate(commands: any[]): Promise<boolean> {
      console.log(commands);
      return Promise.resolve(true);
    }
  }
  let routerStub = new RouterStub();

  beforeEach(() => {
    activatedRoute.testParams = {accountId: 376, entryId: 123};
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [EntryDetailComponent],
      providers: [
        {provide: EntryService, useClass: FakeEntryService},
        {provide: CategoryService, useClass: FakeCategoryService},
        {provide: ActivatedRoute, useValue: activatedRoute},
        {provide: Router, useValue: routerStub}
      ],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(EntryDetailComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    entry = new Entry({
      date: 1483228800000,
      valuta: 1483833600000,
      description: 'Desc1',
      categoryId: 0,
      categoryName: 'Cat1',
      status: 'CLEARED',
      amount: 1000,
      balance: 52300,
      subEntries: [
        new Entry({
          date: 1483228800000,
          valuta: 1483833600000,
          description: 'SubEntry1',
          categoryName: 'Cat2',
          amount: -1700,
          balance: 12665,
        }),
        new Entry({
          date: 1483228800000,
          valuta: 1483833600000,
          description: 'SubEntry2',
          categoryName: 'Cat3',
          amount: 12345,
          balance: 12345,
        })
      ]
    });
    success = true;
  });

  let handleChanges = function () {
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
  };

  it('correctly initialized input fields (including sub-entries)', fakeAsync(() => {
    entry.categoryId = 444;
    handleChanges();
    let entryInputs = de.queryAll(By.css('tr input'));
    expect(entryInputs.length).toBe(6);
    expect(entryInputs[1].nativeElement.value).toBe('Desc1');
    expect(entryInputs[2].nativeElement.value).toBe('10');

    let subEntryInputs = de.queryAll(By.css('p input'));
    expect(subEntryInputs.length).toBe(6);
    expect(subEntryInputs[0].nativeElement.value).toBe('SubEntry1');
    expect(subEntryInputs[2].nativeElement.value).toBe('17');
    expect(subEntryInputs[3].nativeElement.value).toBe('SubEntry2');
    expect(subEntryInputs[4].nativeElement.value).toBe('123.45');
  }));

  it('new entry form', fakeAsync(() => {
    activatedRoute.testParams = {accountId: 376};
    handleChanges();
    let entryInputs = de.queryAll(By.css('tr input'));
    expect(entryInputs.length).toBe(6);
    expect(entryInputs[1].nativeElement.value).toBe('');
    expect(entryInputs[2].nativeElement.value).toBe('');
    expect(entryInputs[3].nativeElement.value).toBe('');
    expect(entryInputs[5].nativeElement.value).toBe('');
  }));

  it('updateIncome() updates ', fakeAsync(() => {
    comp.updateIncome(entry);
    handleChanges();
    let entryInputs = de.queryAll(By.css('tr input'));
    expect(entryInputs.length).toBe(6);
    expect(entryInputs[2].nativeElement.value).toBe('10');
    expect(entryInputs[3].nativeElement.value).toBe('');

    entry.income = 23.45;
    comp.updateIncome(entry);
    handleChanges();
    entryInputs = de.queryAll(By.css('tr input'));
    expect(entryInputs.length).toBe(6);
    expect(entryInputs[2].nativeElement.value).toBe('23.45');
    expect(entryInputs[3].nativeElement.value).toBe('');
  }));

  it('updateExpense() updates', fakeAsync(() => {
    comp.updateExpense(entry);
    handleChanges();
    let entryInputs = de.queryAll(By.css('tr input'));
    expect(entryInputs.length).toBe(6);
    expect(entryInputs[2].nativeElement.value).toBe('10');
    expect(entryInputs[3].nativeElement.value).toBe('');

    entry.expense = 18.90;
    comp.updateExpense(entry);
    handleChanges();
    entryInputs = de.queryAll(By.css('tr input'));
    expect(entryInputs.length).toBe(6);
    expect(entryInputs[2].nativeElement.value).toBe('');
    expect(entryInputs[3].nativeElement.value).toBe('18.9');
  }));

  it('save() of new entries', fakeAsync(() => {
    handleChanges();
    spyOn(routerStub, 'navigate');
    comp.save();
    handleChanges();
    expect(routerStub.navigate).toHaveBeenCalledWith(['/accounts', 376, 'entries']);
  }));

  it('save() of existing entries', fakeAsync(() => {
    handleChanges();
    spyOn(routerStub, 'navigate');
    comp.entry.id = 1141;
    comp.accountId = 1140;
    comp.save();
    handleChanges();
    expect(routerStub.navigate).toHaveBeenCalledWith(['/accounts', 1140, 'entries']);
  }));

  it('delete()', fakeAsync(() => {
    handleChanges();
    spyOn(routerStub, 'navigate');
    comp.delete();
    handleChanges();
    expect(routerStub.navigate).toHaveBeenCalledWith(['/accounts', 376, 'entries']);
  }));

  it('addSubEntry() and removeSubEntry()', fakeAsync(() => {
    entry.categoryId = 444;
    handleChanges();
    expect(de.queryAll(By.css('p')).length).toBe(4);

    comp.addSubEntry();
    handleChanges();
    expect(de.queryAll(By.css('p')).length).toBe(5);

    comp.removeSubEntry(entry.subEntries[0]);
    comp.removeSubEntry(entry.subEntries[0]);
    comp.removeSubEntry(entry.subEntries[0]);
    handleChanges();
    expect(de.queryAll(By.css('p')).length).toBe(2);
  }));

  it('subEntriesTotal() is 0 when subEntries are undefined', fakeAsync(() => {
    handleChanges();
    comp.entry.subEntries = undefined;
    expect(comp.subEntriesTotal()).toBe(0);
  }));

});
