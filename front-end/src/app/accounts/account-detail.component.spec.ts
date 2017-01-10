import {fakeAsync, tick, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement, NO_ERRORS_SCHEMA} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {ActivatedRoute, Router} from '@angular/router';
import {ActivatedRouteStub} from '../testing/route-stubs';
import {AccountService} from './account.service';
import {AccountDetailComponent} from './account-detail.component';
import {Account} from './account';

describe('AccountDetailComponent', () => {
  let activatedRoute = new ActivatedRouteStub();
  let comp: AccountDetailComponent;
  let fixture: ComponentFixture<AccountDetailComponent>;
  let de: DebugElement;

  class FakeAccountService {
    getAccount(id: number): Promise<Account> {
      return Promise.resolve(new Account({id: 0, name: 'Account A'}));
    }

    createAccount(account: Account): Promise<number> {
      return Promise.resolve(1919);
    }

    updateAccount(account: Account): Promise<any> {
      return Promise.resolve(true);
    }

    deleteAccount(accountId): Promise<any> {
      return Promise.resolve(true);
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
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [AccountDetailComponent],
      providers: [
        {provide: AccountService, useClass: FakeAccountService},
        {provide: ActivatedRoute, useValue: activatedRoute},
        {provide: Router, useValue: routerStub}
      ],
      schemas: [NO_ERRORS_SCHEMA]
    });
    fixture = TestBed.createComponent(AccountDetailComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    spyOn(routerStub, 'navigate');
  });

  let handleChanges = function () {
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
  };

  it('form for new account', fakeAsync(() => {
    activatedRoute.testParams = {};
    handleChanges();
    let inputs = de.queryAll(By.css('input'));
    expect(inputs.length).toBe(7);
    inputs.forEach(i => expect(i.nativeElement.value).toBe(''));
  }));

  it('form for existing account', fakeAsync(() => {
    activatedRoute.testParams = {accountId: 376};
    handleChanges();
    let inputs = de.queryAll(By.css('input'));
    expect(inputs.length).toBe(7);
    expect(inputs[0].nativeElement.value).toBe('Account A');
  }));

  it('save() of new account', fakeAsync(() => {
    handleChanges();
    comp.save();
    handleChanges();
    expect(routerStub.navigate).toHaveBeenCalledWith(['/accounts', 1919, 'entries']);
  }));

  it('save() of existing account', fakeAsync(() => {
    handleChanges();
    comp.account.id = 1255;
    comp.save();
    handleChanges();
    expect(routerStub.navigate).toHaveBeenCalledWith(['/accounts', 1255, 'entries']);
  }));

  it('delete() confirmed', fakeAsync(() => {
    let origConfirm = confirm;
    confirm = jasmine.createSpy('confirm').and.returnValue(true);
    handleChanges();

    comp.delete();
    handleChanges();
    expect(confirm).toHaveBeenCalledWith('Really delete this account?');
    confirm = origConfirm;
    expect(routerStub.navigate).toHaveBeenCalledWith(['/accounts']);
  }));

  it('delete() unconfirmed', fakeAsync(() => {
    let origConfirm = confirm;
    confirm = jasmine.createSpy('confirm').and.returnValue(false);
    handleChanges();

    comp.delete();
    handleChanges();
    expect(confirm).toHaveBeenCalledWith('Really delete this account?');
    confirm = origConfirm;
    expect(routerStub.navigate).not.toHaveBeenCalled();
  }));
});
