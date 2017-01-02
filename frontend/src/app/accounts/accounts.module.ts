import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {AccountService} from "./account.service";
import {EntryService} from "./entry.service";
import {AccountsRoutingModule} from "./accounts-routing.module";
import {AccountsComponent} from "./accounts.component";
import {AccountDetailComponent} from "./account-detail.component";
import {EntriesComponent} from "./entries.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    AccountsRoutingModule
  ],
  declarations: [
    AccountsComponent,
    AccountDetailComponent,
    EntriesComponent
  ],
  providers: [
    AccountService,
    EntryService
  ]
})
export class AccountsModule {
}
