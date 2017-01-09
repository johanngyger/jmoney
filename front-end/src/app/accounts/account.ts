export class Account {
  id: number;
  name: string;
  bank: string;
  accountNumber: string;
  startBalance: number;
  minBalance: number;
  abbreviation: string;
  comment: string;

  constructor(fields?: Object) {
    if (fields) {
      Object.assign(this, fields);
    }
  }
}
