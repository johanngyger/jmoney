export class CashFlow {
  categoryId: number;
  categoryName: string;
  income: number;
  expense: number;
  difference: number;
  total: boolean;

  constructor(fields?: Object) {
    if (fields) {
      Object.assign(this, fields);
    }
  }
}
