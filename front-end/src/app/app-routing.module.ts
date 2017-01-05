import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/accounts',
    pathMatch: 'full'
  },
  {
    path: 'accounts',
    loadChildren: 'app/accounts/accounts.module#AccountsModule'
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
