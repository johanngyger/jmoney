import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {OptionsRoutingModule} from "./options-routing.module";
import {OptionsComponent} from "./options.component";
import {InitComponent} from "./init.component";
import {ImportComponent} from "./import.component";
import {OptionsService} from "./options.service";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    OptionsRoutingModule
  ],
  declarations: [
    OptionsComponent,
    InitComponent,
    ImportComponent
  ],
  providers: [
    OptionsService
  ]
})
export class OptionsModule {
}
