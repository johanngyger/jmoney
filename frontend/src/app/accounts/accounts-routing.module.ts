import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {AccountsComponent} from "./accounts.component";
import {AccountDetailComponent} from "./account-detail.component";
import {EntriesComponent} from "./entries.component";

const routes: Routes = [
  {
    path: '',
    component: AccountsComponent,
    children: [{
      path: '',
      children: [
        {path: ':accountId/entries', component: EntriesComponent},
        {path: ':accountId', component: AccountDetailComponent}
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
export class AccountsRoutingModule {
}
