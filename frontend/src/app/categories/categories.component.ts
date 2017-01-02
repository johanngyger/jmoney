import {Component, OnInit} from "@angular/core";
import {CategoryService} from "./category.service";
import {Category} from "./category";

@Component({
  templateUrl: './categories.component.html'
})
export class CategoriesComponent implements OnInit {
  categoryTree: Category;
  loading: boolean;

  constructor(private categoryService: CategoryService) {
  }

  ngOnInit(): void {
    this.fetch();
  }

  fetch(): void {
    this.loading = true;
    this.categoryService.getCategoryTree()
      .then(categoryTree => {
        this.categoryTree = categoryTree;
        this.loading = false;
      })
  }

  save(): void {
    this.categoryService.saveCategoryTree(this.categoryTree)
      .then(result => this.fetch());
    this.categoryTree = null;
  }
}
