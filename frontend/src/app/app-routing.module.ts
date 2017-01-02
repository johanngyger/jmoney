import {NgModule} from "@angular/core";
import {Routes, RouterModule} from "@angular/router";
import {AccountsComponent} from "./account/accounts.component";
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
    loadChildren: 'app/categories/categories.module#CategoriesModule'
  },
  {
    path: 'reports',
    loadChildren: 'app/reports/reports.module#ReportsModule'
  },
  {
    path: 'options',
    loadChildren: 'app/options/options.module#OptionsModule'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
