import {Component, Input} from '@angular/core';
import {Category} from './category';
import {CategoryService} from './category.service';

@Component({
  selector: 'app-jmoney-category-tree',
  templateUrl: './category-tree.component.html'
})
export class CategoryTreeComponent {
  @Input()
  categories: Category[];

  constructor(private categoryService: CategoryService) {
  }

  add(parent: Category): void {
    this.categoryService.createCategory(parent.id)
      .then(newCat => parent.children.unshift(newCat));
  }

  remove(category: Category): void {
    this.categoryService.deleteCategory(category.id)
      .then(newCat => {
        let index = this.categories.findIndex(cat => category.id === cat.id);
        this.categories.splice(index, 1);
      });

  }
}
