import {KeyType} from './key-type.enum';
import {Action} from "./action";
import {KeyEvent} from "./key-event";

export class Key {
  keyEvent: KeyEvent;
  keyType: KeyType;
  actions: Action[] = [];
}
