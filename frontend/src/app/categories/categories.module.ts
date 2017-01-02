import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {CategoryService} from "./category.service";
import {CategoryTreeComponent} from "./category-tree.component";
import {CategoriesComponent} from "./categories.component";
import {CategoriesRoutingModule} from "./categories-routing.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    CategoriesRoutingModule
  ],
  declarations: [
    CategoriesComponent,
    CategoryTreeComponent
  ],
  providers: [
    CategoryService
  ]
})
export class CategoriesModule {
}
