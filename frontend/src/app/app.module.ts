import "./rxjs-extensions";
import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
import {AppComponent} from "./app.component";
import {AppRoutingModule} from "./app-routing.module";
import {AccountsComponent} from "./account/accounts.component";
import {AccountService} from "./account/account.service";
import {EntriesComponent} from "./account/entries.component";
import {EntryService} from "./account/entry.service";
import {AccountDetailComponent} from "./account/account-detail.component";

@NgModule({
  declarations: [
    AppComponent,
    AccountsComponent,
    AccountDetailComponent,
    EntriesComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule
  ],
  providers: [
    AccountService,
    EntryService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
