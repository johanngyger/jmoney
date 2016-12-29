import {NgModule} from "@angular/core";
import {Routes, RouterModule} from "@angular/router";
import {AccountsComponent} from "./account/accounts.component";
import {CategoriesComponent} from "./category/categories.component";
import {OptionsComponent} from "./options/options.component";
import {ReportsComponent} from "./report/reports.component";

const routes: Routes = [
  {
    path: '',
    redirectTo: '/accounts',
    pathMatch: 'full'
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
