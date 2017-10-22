import {fakeAsync, tick, ComponentFixture, TestBed} from '@angular/core/testing';
import {DebugElement} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';
import {CategoryTreeComponent} from './category-tree.component';
import {Category} from './category';
import {CategoryService} from './category.service';

describe('CategoryTreeComponent', () => {
  let comp: CategoryTreeComponent;
  let fixture: ComponentFixture<CategoryTreeComponent>;
  let de: DebugElement;
  const cat11 = new Category({id: 4, name: 'Cat1.1', children: []});
  const cat12 = new Category({id: 5, name: 'Cat1.2', children: []});
  const cat1 = new Category({id: 1, name: 'Cat1', children: [cat11, cat12]});
  const cat2 = new Category({id: 2, name: 'Cat2', children: []});
  const cat31 = new Category({id: 5, name: 'Cat3.1', children: []});
  const cat321 = new Category({id: 5, name: 'Cat3.2.1', children: []});
  const cat322 = new Category({id: 5, name: 'Cat3.2.2', children: []});
  const cat323 = new Category({id: 5, name: 'Cat3.2.3', children: []});
  const cat32 = new Category({id: 1, name: 'Cat3.2', children: [cat321, cat322, cat323]});
  const cat33 = new Category({id: 5, name: 'Cat3.3', children: []});
  const cat3 = new Category({id: 1, name: 'Cat3', children: [cat31, cat32, cat33]});

  class FakeCategoryService {
    createCategory(parentId: number): Promise<Category> {
      return Promise.resolve(new Category({name: 'NewCat'}));
    }

    deleteCategory(id: number): Promise<any> {
      return Promise.resolve();
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [CategoryTreeComponent],
      providers: [{provide: CategoryService, useClass: FakeCategoryService}]
    });
    fixture = TestBed.createComponent(CategoryTreeComponent);
    comp = fixture.componentInstance;
    comp.categories = [cat1, cat2, cat3];
    de = fixture.debugElement;
  })
  ;

  it('can show a tree of categories', fakeAsync(() => {
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    const catList = de.queryAll(By.css('input'));
    expect(catList.length).toBe(11);
    expect(catList[0].nativeElement.value).toBe('Cat1');
    expect(catList[1].nativeElement.value).toBe('Cat1.1');
    expect(catList[2].nativeElement.value).toBe('Cat1.2');
    expect(catList[3].nativeElement.value).toBe('Cat2');
    expect(catList[4].nativeElement.value).toBe('Cat3');
    expect(catList[5].nativeElement.value).toBe('Cat3.1');
    expect(catList[6].nativeElement.value).toBe('Cat3.2');
    expect(catList[7].nativeElement.value).toBe('Cat3.2.1');
    expect(catList[8].nativeElement.value).toBe('Cat3.2.2');
    expect(catList[9].nativeElement.value).toBe('Cat3.2.3');
    expect(catList[10].nativeElement.value).toBe('Cat3.3');
  }));

  it('can add a category', fakeAsync(() => {
    comp.add(cat2);
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    const catList = de.queryAll(By.css('input'));
    expect(catList.length).toBe(12);
    expect(catList[4].nativeElement.value).toBe('NewCat');
  }));

  it('can remove a category', fakeAsync(() => {
    comp.remove(cat1);
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    tick();
    fixture.detectChanges();
    const catList = de.queryAll(By.css('input'));
    expect(catList.length).toBe(9);
    expect(catList[0].nativeElement.value).toBe('Cat2');
  }));

});
