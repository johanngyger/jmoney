import "rxjs/add/operator/switchMap";
import {Component, OnInit} from "@angular/core";
import {ActivatedRoute} from "@angular/router";
import {ReportsService} from "./reports.service";
import {Entry} from "../account/entry";

@Component({
  templateUrl: './consistency.component.html'
})
export class ConsistencyComponent implements OnInit {
  splitEntries: Entry[];
  entriesWithoutCategory: Entry[];

  constructor(private reportsService: ReportsService) {
  }

  ngOnInit(): void {
    this.reportsService.getInconsistentSplitEntries()
      .then(entries => this.splitEntries = entries);

    this.reportsService.getEntriesWithoutCategory()
      .then(entries => this.entriesWithoutCategory = entries);
  }
}
