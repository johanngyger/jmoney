import {Component} from "@angular/core";
import {OptionsService} from "./options.service";

@Component({
  templateUrl: './init.component.html'
})
export class InitComponent {
  status: string;

  constructor(private optionsService: OptionsService) {
  }

  init(): void {
    this.optionsService.init()
      .then(response => this.status = 'success')
      .catch(response => this.status = 'error');
  }

}
