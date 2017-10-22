import {fakeAsync, tick, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement, NO_ERRORS_SCHEMA} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {Subject} from 'rxjs/Subject';
import {AccountsComponent} from './accounts.component';
import {Account} from './account';
import {AccountService} from './account.service';

describe('AccountsComponent', () => {
  let comp: AccountsComponent;
  let fixture: ComponentFixture<AccountsComponent>;
  let de: DebugElement;
  let success: boolean;
  let accounts: Account[];
  const accountSubject = new Subject<number>();

  class FakeAccountService {
    accountChange = accountSubject;

    getAccounts(): Promise<Account[]> {
      return success ? Promise.resolve(accounts) : Promise.reject(success);
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [AccountsComponent],
      providers: [{provide: AccountService, useClass: FakeAccountService}],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(AccountsComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    accounts = [
      new Account({name: 'Account A'}),
      new Account({name: 'Account B'}),
      new Account({name: 'Account C'})
    ];
  });

  const handleChanges = function () {
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
  };

  it('can get accounts and show them', fakeAsync(() => {
    success = true;
    handleChanges();
    const entryElems = de.queryAll(By.css('a'));
    expect(entryElems.length).toBe(4);
    expect(entryElems[0].nativeElement.textContent).toBe('Account A');
    expect(entryElems[1].nativeElement.textContent).toBe('Account B');
    expect(entryElems[2].nativeElement.textContent).toBe('Account C');
  }));

  it('can show an error message when account retrieval fails', fakeAsync(() => {
    success = false;
    handleChanges();
    expect(de.query(By.css('.alert-error'))).toBeTruthy();
  }));

  it('can handle account change events', fakeAsync(() => {
    success = true;
    handleChanges();
    let entryElems = de.queryAll(By.css('a'));
    expect(entryElems.length).toBe(4);
    expect(entryElems[0].nativeElement.textContent).toBe('Account A');
    expect(entryElems[1].nativeElement.textContent).toBe('Account B');
    expect(entryElems[2].nativeElement.textContent).toBe('Account C');

    accounts = [new Account({name: 'Account X'})];
    accountSubject.next(1);
    handleChanges();
    entryElems = de.queryAll(By.css('a'));
    expect(entryElems.length).toBe(2);
    expect(entryElems[0].nativeElement.textContent).toBe('Account X');
  }));

  it('can handle account selection', () => {
    comp.onSelected(17);
    expect(comp.accountId).toBe(17);
  });

});

