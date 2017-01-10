import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {EntryService} from './entry.service';
import {Entry} from './entry';
import {CategoryService} from '../categories/category.service';
import {Category} from '../categories/category';

@Component({
  templateUrl: './entry-detail.component.html'
})
export class EntryDetailComponent implements OnInit {
  accountId: number;
  entry: Entry;
  categories: Category[];
  splitCategory: Category;

  entryStates = [
    {id: null, label: ''},
    {id: 'RECONCILING', label: 'Reconciling'},
    {id: 'CLEARED', label: 'Cleared'}
  ];

  constructor(private entryService: EntryService, private categoryService: CategoryService,
              private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    this.categoryService.getCategories()
      .then(categories => this.categories = categories);

    this.categoryService.getSplitCategory()
      .then(splitCategory => this.splitCategory = splitCategory);

    this.route.params
      .switchMap(params => {
        this.accountId = +params['accountId'];
        let entryId = params['entryId'];
        if (entryId) {
          return this.entryService.getEntry(this.accountId, +entryId);
        } else {
          return Promise.resolve(new Entry());
        }
      })
      .subscribe(entry => {
        this.initAmount(entry);
        entry.subEntries.forEach(e => this.initAmount(e));
        this.entry = entry;
      });
  }

  initAmount(entry) {
    if (entry.amount >= 0) {
      entry.income = entry.amount / 100;
    } else {
      entry.expense = -entry.amount / 100;
    }
  }

  updateIncome(entry: Entry): void {
    if (entry.income) {
      entry.amount = entry.income * 100;
      entry.expense = null;
    }
  }

  updateExpense(entry: Entry): void {
    if (entry.expense) {
      entry.amount = entry.expense * -100;
      entry.income = null;
    }
  }

  addSubEntry(): void {
    let e = new Entry();
    e.amount = 0;
    this.entry.subEntries.push(e);
  }

  removeSubEntry(subEntry: Entry): void {
    let index = this.entry.subEntries.indexOf(subEntry);
    this.entry.subEntries.splice(index, 1);
  }

  save(): void {
    if (this.entry.id) {
      this.entryService.updateEntry(this.accountId, this.entry)
        .then(() => this.router.navigate(['/accounts', this.accountId, 'entries']));
    } else {
      this.entryService.createEntry(this.accountId, this.entry)
        .then(() => this.router.navigate(['/accounts', this.accountId, 'entries']));
    }
  }

  delete(): void {
    this.entryService.deleteEntry(this.accountId, this.entry.id)
      .then(() => this.router.navigate(['/accounts', this.accountId, 'entries']));
  }

  subEntriesTotal(): number {
    return this.entry.subEntries ? this.entry.subEntries.reduce((acc, entry) => acc + entry.amount, 0) : 0;
  }

}
