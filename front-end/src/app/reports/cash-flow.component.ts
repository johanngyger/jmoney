import "rxjs/add/operator/switchMap";
import {Component, OnInit} from "@angular/core";
import {ReportsService} from "./reports.service";
import {CashFlow} from "./cash-flow";
import * as moment from "moment";

@Component({
  templateUrl: './cash-flow.component.html'
})
export class CashFlowComponent implements OnInit {
  cashFlows: CashFlow[];
  periods = [
    {value: 'thisMonth', description: 'This month'},
    {value: 'thisYear', description: 'This year'},
    {value: 'lastMonth', description: 'Last month'},
    {value: 'lastYear', description: 'Last year'}
  ];
  period = 'thisMonth';
  fromDate: string;
  toDate: string;
  private error: boolean;

  constructor(private reportsService: ReportsService) {
  }

  ngOnInit(): void {
    this.onPeriodChange();
  }

  fetchCashFlow(): void {
    this.reportsService.getCashFlow(this.fromDate, this.toDate)
      .then(cashFlows => this.cashFlows = cashFlows)
      .catch(reason => this.error = true);
  }

  onPeriodChange(): void {
    switch (this.period) {
      case "thisMonth":
        this.fromDate = moment().startOf('month').format("YYYY-MM-DD");
        this.toDate = moment().endOf('month').format("YYYY-MM-DD");
        break;
      case "thisYear":
        this.fromDate = moment().startOf('year').format("YYYY-MM-DD");
        this.toDate = moment().endOf('year').format("YYYY-MM-DD");
        break;
      case "lastMonth":
        this.fromDate = moment().subtract(1, 'month').startOf('month').format("YYYY-MM-DD");
        this.toDate = moment().subtract(1, 'month').endOf('month').format("YYYY-MM-DD");
        break;
      case "lastYear":
        this.fromDate = moment().subtract(1, 'year').startOf('year').format("YYYY-MM-DD");
        this.toDate = moment().subtract(1, 'year').endOf('year').format("YYYY-MM-DD");
        break;
    }

    this.fetchCashFlow();
  }

}
