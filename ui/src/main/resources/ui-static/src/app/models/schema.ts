import {Key} from './key';

export class Schema {
  id: string;
  createdBy: string;
  createdDate: Date;
  meta: string;
  keys: Key[];
}
