import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import 'rxjs/add/operator/toPromise';
import {Category} from './category';

@Injectable()
export class CategoryService {
  constructor(private http: Http) {
  }

  getCategoryTree(): Promise<Category> {
    return this.http
      .get('/api/category-tree')
      .toPromise()
      .then(response => response.json() as Category);
  }

  saveCategoryTree(rootCategory: Category): Promise<any> {
    return this.http
      .put('/api/category-tree', rootCategory)
      .toPromise();
  }

  getCategories(): Promise<Category[]> {
    return this.http
      .get('/api/categories')
      .toPromise()
      .then(response => response.json() as Category[]);
  }

  createCategory(parentId: number): Promise<Category> {
    let newCat = new Category();
    newCat.name = '<New category>';
    newCat.parentId = parentId;

    return this.http.post('/api/categories', newCat)
      .toPromise()
      .then(response => {
        newCat.id = response.json();
        return newCat;
      });
  }

  deleteCategory(id: number): Promise<any> {
    return this.http
      .delete('/api/categories/' + id)
      .toPromise();
  }

  getSplitCategory(): Promise<Category> {
    return this.http
      .get('/api/split-category')
      .toPromise()
      .then(result => result.json() as Category);

  }
}
