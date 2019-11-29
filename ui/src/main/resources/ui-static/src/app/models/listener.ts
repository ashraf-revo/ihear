import {ListenerType} from './listener-type.enum';
import {Action} from './action';

export class Listener {
  listenerType: ListenerType;
  actions: Action[];
}
