import {ComponentFixture, TestBed} from '@angular/core/testing';
import {By}              from '@angular/platform-browser';
import {DebugElement}    from '@angular/core';
import {InitComponent} from './init.component';

describe('Init component', () => {

  let comp: InitComponent;
  let fixture: ComponentFixture<InitComponent>;
  let de: DebugElement;
  let el: HTMLElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InitComponent],
    });
    fixture = TestBed.createComponent(InitComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement.query(By.css('h1'));
    el = de.nativeElement;
  });

  it('no title in the DOM until manually call `detectChanges`', () => {
    expect(el.textContent).toEqual('');
  });

  it('should display original title', () => {
    fixture.detectChanges();
    expect(el.textContent).toContain(comp.title);
  });

  it('should display a different test title', () => {
    comp.title = 'Test Title';
    fixture.detectChanges();
    expect(el.textContent).toContain('Test Title');
  });
});
