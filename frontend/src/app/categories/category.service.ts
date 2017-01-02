import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import "rxjs/add/operator/toPromise";
import {Category} from "./category";

@Injectable()
export class CategoryService {
  constructor(private http: Http) {
  }

  getCategoryTree(): Promise<Category> {
    return this.http
      .get('/rest/category-tree')
      .toPromise()
      .then(response => response.json() as Category)
  }

  saveCategoryTree(rootCategory: Category): Promise<any> {
    return this.http
      .put('/rest/category-tree', rootCategory)
      .toPromise()
  }

  createCategory(parentId: number): Promise<Category> {
    let newCat = new Category();
    newCat.name = '<New category>';
    newCat.parentId = parentId;

    return this.http.post('/rest/categories', newCat)
      .toPromise()
      .then(response => {
        newCat.id = response.json();
        return newCat;
      });
  }

  deleteCategory(id: number): Promise<any> {
    return this.http
      .delete('/rest/categories/' + id)
      .toPromise();
  }
}
