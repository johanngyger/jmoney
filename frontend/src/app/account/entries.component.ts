import "rxjs/add/operator/switchMap";
import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Params} from "@angular/router";
import {Entry} from "./entry";
import {EntryService} from "./entry.service";

@Component({
  templateUrl: './entries.component.html'
})
export class EntriesComponent implements OnInit {
  entries: Entry[];

  constructor(private entryService: EntryService,
              private route: ActivatedRoute,) {
  }

  ngOnInit(): void {
    this.route.params
      .switchMap((params: Params) => this.entryService.getEntries(+params['accountId']))
      .subscribe(entries => this.entries = entries);
  }

  getStatusText(status): string {
    if (status === "RECONCILING") {
      return "A";
    } else if (status === "CLEARED") {
      return "V";
    } else {
      return null;
    }
  }

}
