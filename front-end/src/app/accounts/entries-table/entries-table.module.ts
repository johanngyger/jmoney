import {NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";
import {CommonModule} from "@angular/common";
import {EntriesTableComponent} from "./entries-table.component";

@NgModule({
  imports: [
    CommonModule,
    RouterModule
  ],
  declarations: [
    EntriesTableComponent
  ],
  exports: [
    EntriesTableComponent
  ]
})
export class EntriesTableModule {
}
