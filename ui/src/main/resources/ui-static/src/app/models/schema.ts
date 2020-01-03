import {Event} from "./event";

export class Schema {
  id: string;
  createdBy: string;
  createdDate: Date;
  title: string;
  meta: string;
  event: Event = new Event();
}
