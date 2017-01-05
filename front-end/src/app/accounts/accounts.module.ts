import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {AccountService} from "./account.service";
import {EntryService} from "./entry.service";
import {AccountsRoutingModule} from "./accounts-routing.module";
import {AccountsComponent} from "./accounts.component";
import {AccountDetailComponent} from "./account-detail.component";
import {EntriesComponent} from "./entries.component";
import {EntryDetailComponent} from "./entry-detail.component";
import {CategoryService} from "../categories/category.service";
import {EntriesTableModule} from "./entries-table/entries-table.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    AccountsRoutingModule,
    EntriesTableModule
  ],
  declarations: [
    AccountsComponent,
    AccountDetailComponent,
    EntriesComponent,
    EntryDetailComponent
  ],
  providers: [
    AccountService,
    EntryService,
    CategoryService
  ]
})
export class AccountsModule {
}
