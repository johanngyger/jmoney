import "rxjs/add/operator/switchMap";
import {Component, OnInit} from "@angular/core";
import {ActivatedRoute, Params} from "@angular/router";
import {Entry} from "./entry";
import {EntryService} from "./entry.service";

@Component({
  templateUrl: './entries.component.html'
})
export class EntriesComponent implements OnInit {
  private entries: Entry[];
  private accountId: number;

  constructor(private entryService: EntryService,
              private route: ActivatedRoute,) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => this.accountId = params['accountId']);

    this.route.params
      .switchMap(params => this.entryService.getEntries(+params['accountId']))
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
