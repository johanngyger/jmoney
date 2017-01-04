import "rxjs/add/operator/switchMap";
import {Component, OnInit} from "@angular/core";
import {ReportsService} from "./reports.service";
import {Entry} from "../accounts/entry";

@Component({
  templateUrl: './consistency.component.html'
})
export class ConsistencyComponent implements OnInit {
  splitEntries: Entry[];
  entriesWithoutCategory: Entry[];
  private error: boolean;

  constructor(private reportsService: ReportsService) {
  }

  ngOnInit(): void {
    this.reportsService.getInconsistentSplitEntries()
      .then(entries => this.splitEntries = entries)
      .catch(reason => this.error = true);

    this.reportsService.getEntriesWithoutCategory()
      .then(entries => this.entriesWithoutCategory = entries)
      .catch(reason => this.error = true);
  }
}
