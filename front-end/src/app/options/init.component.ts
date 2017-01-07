import {Component} from '@angular/core';
import {OptionsService} from './options.service';

@Component({
  templateUrl: './init.component.html'
})
export class InitComponent {
  status: string;
  loading: boolean;

  constructor(private optionsService: OptionsService) {
  }

  init(): void {
    this.loading = true;

    this.optionsService.init()
      .then(() => {
        this.status = 'success';
        this.loading = false;
      }, () => {
        this.status = 'error';
        this.loading = false;
      });
  }

}
