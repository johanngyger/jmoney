import {Component} from '@angular/core';
import {NgUploaderOptions} from 'ngx-uploader';

@Component({
  templateUrl: './import.component.html'
})
export class ImportComponent {
  status: string;
  loading: boolean;
  options: NgUploaderOptions = new NgUploaderOptions({url: '/api/options/import'});

  beforeUpload(uploadingFile): void {
    this.loading = true;
    this.status = '';
  }

  handleUpload(data): void {
    if (data.status === 200) {
      this.status = 'success';
      this.loading = false;
    } else {
      this.status = 'error';
      this.loading = false;
    }
  }
}
