import {Component} from '@angular/core';
import * as packageJson from '../../package.json';

@Component({
  selector: 'app-jmoney-root',
  templateUrl: './app.component.html'
})
export class AppComponent {
  version = (<any>packageJson).version;
}
