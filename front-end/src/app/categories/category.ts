export class Category {
  id: number;
  name: string;
  nameIndented: string;
  type: string;
  parentId: number;
  categoryName: string;
  children: Category[];

  constructor(fields?: Object) {
    if (fields) {
      Object.assign(this, fields);
    }
  }
}
