import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ReportsRoutingModule} from './reports-routing.module';
import {ReportsComponent} from './reports.component';
import {BalancesComponent} from './balances.component';
import {ReportsService} from './reports.service';
import {CashFlowComponent} from './cash-flow.component';
import {ConsistencyComponent} from './consistency.component';
import {EntriesTableModule} from '../accounts/entries-table/entries-table.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReportsRoutingModule,
    EntriesTableModule
  ],
  declarations: [
    ReportsComponent,
    BalancesComponent,
    CashFlowComponent,
    ConsistencyComponent
  ],
  providers: [ReportsService]
})
export class ReportsModule {
}
