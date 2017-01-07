import {ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {ImportComponent} from './import.component';
import {NgUploaderModule} from 'ngx-uploader';

describe('ImportComponent', () => {
  let comp: ImportComponent;
  let fixture: ComponentFixture<ImportComponent>;
  let divSuccess: DebugElement;
  let divError: DebugElement;
  let imgLoading: DebugElement;
  let de: DebugElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ImportComponent],
      imports: [NgUploaderModule]
    });
    fixture = TestBed.createComponent(ImportComponent);
    comp = fixture.componentInstance;
    de = fixture.debugElement;
    fetchElements();
  });

  function fetchElements() {
    divSuccess = de.query(By.css('.alert-success'));
    divError = de.query(By.css('.alert-error'));
    imgLoading = de.query(By.css('img'));
  }

  it('can show loading status', () => {
    expect(imgLoading).toBeNull();
    comp.beforeUpload('file');
    fixture.detectChanges();
    fetchElements();
    expect(imgLoading.nativeElement).toBeTruthy();
  });

  it('handle the upload (positive case)', () => {
    expect(divSuccess).toBeNull();
    comp.handleUpload({status: 200});
    fixture.detectChanges();
    fetchElements();
    expect(divSuccess.nativeElement).toBeTruthy();
    expect(divError).toBeNull();
    expect(imgLoading).toBeNull();
  });

  it('handle the upload (negative case)', () => {
    expect(divError).toBeNull();
    comp.handleUpload({status: 500});
    fixture.detectChanges();
    fetchElements();
    expect(divError.nativeElement).toBeTruthy();
    expect(divSuccess).toBeNull();
    expect(imgLoading).toBeNull();
  });

});
