import "rxjs/add/operator/switchMap";
import {Component, OnInit} from "@angular/core";
import {Balance} from "./balance";
import {ReportsService} from "./reports.service";

@Component({
  templateUrl: './balances.component.html'
})
export class BalancesComponent implements OnInit {
  filter = 'date';
  filterDate: string;
  balances: Balance[];

  constructor(private reportsService: ReportsService) {
  }

  ngOnInit(): void {
    this.fetchBalances();
  }

  fetchBalances(): void {
    this.reportsService.getBalances(this.filterDate)
      .then(balances => this.balances = balances);
  }
}
