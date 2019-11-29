import {Listener} from './listener';
import {KeyType} from './key-type.enum';

export class Key {
  keyType: KeyType;
  listener: Listener[];
}
