export class Entry<T> {
  identifer: any;
  data: T;

  constructor(identifer: any, data: T) {
    this.identifer = identifer;
    this.data = data;
  }
}
