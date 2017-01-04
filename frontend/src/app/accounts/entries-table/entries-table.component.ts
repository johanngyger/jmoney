import {Component, Input} from "@angular/core";
import {Entry} from "../entry";

@Component({
  selector: 'jmoney-entries-table',
  templateUrl: './entries-table.component.html'
})
export class EntriesTableComponent {
  @Input()
  private entries: Entry[];

  getStatusText(status): string {
    if (status === "RECONCILING") {
      return "R";
    } else if (status === "CLEARED") {
      return "C";
    } else {
      return null;
    }
  }

}
