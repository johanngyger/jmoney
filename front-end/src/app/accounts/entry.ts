export class Entry {
  id: number;
  status: string;
  creation: number;
  date: number;
  valuta: number;
  description: string;
  amount: number;
  income: number;
  expense: number;
  memo: string;
  balance: number;
  accountId: number;
  categoryId: number;
  categoryName: string;
  subEntries: Entry[] = [];

  constructor(fields?: Object) {
    if (fields) {
      Object.assign(this, fields);
    }
  }
}
