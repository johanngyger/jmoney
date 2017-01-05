import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ReportsComponent} from './reports.component';
import {BalancesComponent} from './balances.component';
import {CashFlowComponent} from './cash-flow.component';
import {ConsistencyComponent} from './consistency.component';

const routes: Routes = [
  {
    path: '',
    component: ReportsComponent,
    children: [{
      path: '',
      children: [
        {path: 'balances', component: BalancesComponent},
        {path: 'cash-flow', component: CashFlowComponent},
        {path: 'consistency', component: ConsistencyComponent}
      ]
    }]
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [
    RouterModule
  ]
})
export class ReportsRoutingModule {
}
