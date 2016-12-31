import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {InitComponent} from "./init.component";
import {ImportComponent} from "./import.component";
import {OptionsComponent} from "./options.component";

const routes: Routes = [
  {
    path: '',
    component: OptionsComponent,
    children: [{
      path: '',
      children: [
        {path: 'init', component: InitComponent},
        {path: 'import', component: ImportComponent},
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
export class OptionsRoutingModule {
}
