import {KeyType} from './key-type.enum';
import {Action} from "./action";

export class Key {
  keyType: KeyType;
  actions: Action[] = [];
}
