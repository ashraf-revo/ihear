export class Entry<T> {
  identifer: string;
  data: T;

  constructor(identifer: any, data: T) {
    this.identifer = identifer;
    this.data = data;
  }
}
