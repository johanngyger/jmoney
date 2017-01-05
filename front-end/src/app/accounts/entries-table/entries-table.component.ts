import {Component, Input} from '@angular/core';
import {Entry} from '../entry';

@Component({
  selector: 'app-jmoney-entries-table',
  templateUrl: './entries-table.component.html'
})
export class EntriesTableComponent {
  @Input()
  entries: Entry[];

  getStatusText(status): string {
    if (status === 'RECONCILING') {
      return 'R';
    } else if (status === 'CLEARED') {
      return 'C';
    } else {
      return null;
    }
  }

}
