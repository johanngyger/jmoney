export class Balance {
  accountName: string;
  balance: number;
  total: boolean;

  constructor(fields?: Object) {
    if (fields) {
      Object.assign(this, fields);
    }
  }
}
