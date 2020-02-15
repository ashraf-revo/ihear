import {Key} from "./key";

export class Schema {
  id: string;
  createdBy: string;
  createdDate: Date;
  title: string;
  meta: string;
  keys: Key[] = [];
}
