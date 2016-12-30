import {NgModule} from "@angular/core";
import {Routes, RouterModule} from "@angular/router";
import {AccountsComponent} from "./account/accounts.component";
import {CategoriesComponent} from "./category/categories.component";
import {OptionsComponent} from "./options/options.component";
import {ReportsComponent} from "./report/reports.component";
import {EntriesComponent} from "./account/entries.component";
import {AccountDetailComponent} from "./account/account-detail.component";

const routes: Routes = [
  {
    path: '',
    redirectTo: '/accounts',
    pathMatch: 'full'
  },
  {
    path: 'accounts/:accountId/entries',
    component: EntriesComponent
  },
  {
    path: 'accounts/:accountId',
    component: AccountDetailComponent
  },
  {
    path: 'accounts',
    component: AccountsComponent
  },
  {
    path: 'categories',
    component: CategoriesComponent
  },
  {
    path: 'reports',
    component: ReportsComponent
  },
  {
    path: 'options',
    component: OptionsComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
