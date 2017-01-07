import {fakeAsync, tick, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {InitComponent} from './init.component';
import {OptionsService} from './options.service';

describe('InitComponent', () => {
  let comp: InitComponent;
  let fixture: ComponentFixture<InitComponent>;
  let divSuccess: DebugElement;
  let divError: DebugElement;
  let imgLoading: DebugElement;
  let buttonSubmit: DebugElement;
  let initSuccess: boolean;
  let de: DebugElement;

  class FakeOptionsService {
    init(): Promise<any> {
      return initSuccess ? Promise.resolve(initSuccess) : Promise.reject(initSuccess);
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InitComponent],
      providers: [{provide: OptionsService, useClass: FakeOptionsService}]
    });
    fixture = TestBed.createComponent(InitComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    fetchElements();
  });

  function fetchElements() {
    buttonSubmit = de.query(By.css('button'));
    divSuccess = de.query(By.css('.alert-success'));
    divError = de.query(By.css('.alert-error'));
    imgLoading = de.query(By.css('img'));
  }

  it('can show init button', () => {
    expect(buttonSubmit.nativeElement.textContent).toEqual('Initialize');
  });

  it('can show success message', () => {
    comp.status = 'success';
    fixture.detectChanges();
    fetchElements();
    expect(divSuccess.nativeElement.textContent).toContain('successful');
  });

  it('can show success message with fake service', fakeAsync(() => {
    initSuccess = true;
    expect(imgLoading).toBeNull();
    comp.init();
    fixture.detectChanges();
    fetchElements();
    expect(divError).toBeNull();
    expect(divSuccess).toBeNull();
    expect(imgLoading.nativeElement).toBeTruthy();

    tick();
    fixture.detectChanges();
    fetchElements();
    expect(imgLoading).toBeNull();
    expect(divError).toBeNull();
    expect(divSuccess.nativeElement).toBeTruthy();
  }));

  it('can show error message with fake service', fakeAsync(() => {
    initSuccess = false;
    comp.init();
    fixture.detectChanges();
    fetchElements();
    expect(divError).toBeNull();
    expect(divSuccess).toBeNull();
    expect(imgLoading.nativeElement).toBeTruthy();

    tick();
    fixture.detectChanges();
    fetchElements();
    expect(imgLoading).toBeNull();
    expect(divSuccess).toBeNull();
    expect(divError.nativeElement).toBeTruthy();
  }));
}
