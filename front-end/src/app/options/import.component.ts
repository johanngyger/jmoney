import {Component, EventEmitter} from '@angular/core';
import {UploadOutput, UploadInput, UploadFile, UploaderOptions} from 'ngx-uploader';

@Component({
  templateUrl: './import.component.html'
})
export class ImportComponent {
  options: UploaderOptions;
  status: string;
  loading: boolean;
  uploadInput: EventEmitter<UploadInput>;

  constructor() {
    this.uploadInput = new EventEmitter<UploadInput>(); // input events, we use this to emit data to ngx-uploader
  }

  onUploadOutput(output: UploadOutput): void {
    if (output.type === 'allAddedToQueue') {
      this.loading = true;
      this.status = '';
      const event: UploadInput = {
        type: 'uploadAll',
        url: '/api/options/import',
        method: 'POST',
      };
      this.uploadInput.emit(event);
    } else if (output.type === 'done') {
      if (output.file.responseStatus === 200) {
        this.status = 'success';
        this.loading = false;
      } else {
        this.status = 'error';
        this.loading = false;
      }
    }
  }
}
