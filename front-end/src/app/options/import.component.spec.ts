import {ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {ImportComponent} from './import.component';
import {NgUploaderModule, UploadFile, UploadOutput} from 'ngx-uploader';

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

  it('can handle a successful upload', () => {
    expect(divSuccess).toBeNull();
    const uploadOutput = <UploadOutput>{};
    uploadOutput.type = 'done';
    uploadOutput.file = <UploadFile>{};
    uploadOutput.file.responseStatus = 200;
    comp.onUploadOutput(uploadOutput);
    fixture.detectChanges();
    fetchElements();
    expect(divSuccess.nativeElement).toBeTruthy();
    expect(divError).toBeNull();
    expect(imgLoading).toBeNull();
  });

  it('can handle a failure in the upload', () => {
    expect(divSuccess).toBeNull();
    const uploadOutput = <UploadOutput>{};
    uploadOutput.type = 'done';
    uploadOutput.file = <UploadFile>{};
    uploadOutput.file.responseStatus = 500;
    comp.onUploadOutput(uploadOutput);
    fixture.detectChanges();
    fetchElements();
    expect(divError.nativeElement).toBeTruthy();
    expect(divSuccess).toBeNull();
    expect(imgLoading).toBeNull();
  });

  it('can start an upload', () => {
    expect(divSuccess).toBeNull();
    comp.uploadInput.subscribe((input) => console.log('EVENT: ' + JSON.stringify(input)));
    const uploadOutput = <UploadOutput>{};
    uploadOutput.type = 'allAddedToQueue';
    comp.onUploadOutput(uploadOutput);
    fixture.detectChanges();
    fetchElements();
    expect(divSuccess).toBeNull();
    expect(divError).toBeNull();
    expect(imgLoading.nativeElement).toBeTruthy();
  });

});
