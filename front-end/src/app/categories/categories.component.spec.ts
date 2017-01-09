import {fakeAsync, tick, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {CategoriesComponent} from './categories.component';
import {CategoryTreeComponent} from './category-tree.component';
import {Category} from './category';
import {CategoryService} from './category.service';

describe('CategoriesComponent', () => {
  let comp: CategoriesComponent;
  let fixture: ComponentFixture<CategoriesComponent>;
  let de: DebugElement;
  let rootCat: Category;
  let success: boolean;

  let createCategoryTree = function () {
    let cat11 = new Category({id: 1, name: 'Cat1.1', children: []});
    let cat12 = new Category({id: 2, name: 'Cat1.2', children: []});
    let cat1 = new Category({id: 3, name: 'Cat1', children: [cat11, cat12]});
    let cat2 = new Category({id: 4, name: 'Cat2', children: []});
    let cat31 = new Category({id: 5, name: 'Cat3.1', children: []});
    let cat321 = new Category({id: 6, name: 'Cat3.2.1', children: []});
    let cat322 = new Category({id: 7, name: 'Cat3.2.2', children: []});
    let cat323 = new Category({id: 8, name: 'Cat3.2.3', children: []});
    let cat32 = new Category({id: 9, name: 'Cat3.2', children: [cat321, cat322, cat323]});
    let cat33 = new Category({id: 10, name: 'Cat3.3', children: []});
    let cat3 = new Category({id: 11, name: 'Cat3', children: [cat31, cat32, cat33]});
    return new Category({id: 0, name: 'Cat3', children: [cat1, cat2, cat3]});
  };

  class FakeCategoryService {
    getCategoryTree(): Promise<Category> {
      return success ? Promise.resolve(rootCat) : Promise.reject(success);
    }

    saveCategoryTree(rootCategory: Category): Promise<any> {
      return success ? Promise.resolve() : Promise.reject(success);
    }

    createCategory(parentId: number): Promise<Category> {
      return success ? Promise.resolve(new Category({name: 'NewCat'})) : Promise.reject(success);
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [CategoriesComponent, CategoryTreeComponent],
      providers: [{provide: CategoryService, useClass: FakeCategoryService}]
    });
    fixture = TestBed.createComponent(CategoriesComponent);
    comp = fixture.componentInstance;
    rootCat = createCategoryTree();
    comp.categoryTree = rootCat;
    de = fixture.debugElement;
  });

  let handleChanges = function () {
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
  };

  it('can get the categoryTree and show a tree of categories', fakeAsync(() => {
    success = true;
    handleChanges();
    let catList = de.queryAll(By.css('input'));
    expect(catList.length).toBe(11);
  }));

  it('can handle a failure in getting the categoryTree and show an error message', fakeAsync(() => {
    success = false;
    handleChanges();
    expect(de.query(By.css('.alert-error'))).toBeTruthy();
  }));

  it('can add a main category', fakeAsync(() => {
    success = true;
    comp.add();
    handleChanges();
    let catList = de.queryAll(By.css('input'));
    expect(catList.length).toBe(12);
  }));

  it('can add a main category (error case)', fakeAsync(() => {
    success = false;
    comp.add();
    handleChanges();
    let catList = de.queryAll(By.css('input'));
    expect(de.query(By.css('.alert-error'))).toBeTruthy();
    expect(catList.length).toBe(11);
  }));

  it('can save the category tree', fakeAsync(() => {
    success = true;
    comp.save();
    handleChanges();
    expect(de.queryAll(By.css('input')).length).toBe(11);
  }));

  it('can save the category tree (error case)', fakeAsync(() => {
    success = false;
    comp.save();
    handleChanges();
    expect(de.query(By.css('.alert-error'))).toBeTruthy();
    expect(de.queryAll(By.css('input')).length).toBe(11);
  }));
});

