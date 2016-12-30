import {NgModule} from "@angular/core";
import {CommonModule, DatePipe} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {ReportsRoutingModule} from "./reports-routing.module";
import {ReportsComponent} from "./reports.component";
import {BalancesComponent} from "./balances.component";
import {ReportsService} from "./reports.service";
import {CashFlowComponent} from "./cash-flow.component";
import {ConsistencyComponent} from "./consistency.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReportsRoutingModule
  ],
  declarations: [
    ReportsComponent,
    BalancesComponent,
    CashFlowComponent,
    ConsistencyComponent
  ],
  providers: [
    ReportsService,
    DatePipe
  ]
})
export class ReportModule {
}
