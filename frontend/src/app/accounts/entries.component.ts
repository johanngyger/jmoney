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
  private filter = '';

  private entryCount: number;
  private page = 1;
  private maxPage: number;


  constructor(private entryService: EntryService,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => this.accountId = params['accountId']);

    this.route.queryParams.subscribe(queryParams => {
      this.filter = queryParams['filter'] || '';
      this.page = +queryParams['page'] || 1;
    });

    this.load(this.page);
  }

  load(page: number): void {
    this.page = page;

    this.route.params
      .switchMap(params => this.entryService.getEntryCount(+params['accountId'], this.filter, this.page))
      .subscribe(count => {
        this.entryCount = count;
        this.maxPage = Math.ceil(this.entryCount / 10)
      });

    this.route.params
      .switchMap(params => this.entryService.getEntries(+params['accountId'], this.filter, this.page))
      .subscribe(entries => this.entries = entries);
  }

  prevPage(): number {
    return Math.max(this.page - 1, 1);
  }

  nextPage(): number {
    return Math.min(this.page + 1, this.maxPage);
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
