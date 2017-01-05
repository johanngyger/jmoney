import {Component} from '@angular/core';
import {NgUploaderOptions} from 'ngx-uploader';

@Component({
  templateUrl: './import.component.html'
})
export class ImportComponent {
  status: string;
  loading: boolean;
  options: NgUploaderOptions = {
    url: '/rest/options/import2'
  };

  beforeUpload(uploadingFile): void {
    this.loading = true;
    this.status = '';
  }

  handleUpload(data): void {
    if (data.status !== 200) {
      this.status = 'error';
      this.loading = false;
    } else if (data.status === 200) {
      this.status = 'success';
      this.loading = false;
    }
  }
}
