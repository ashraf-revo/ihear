import {ListenerType} from './listener-type.enum';
import {Action} from './action';

export class Listener {
  listenerType: ListenerType;
  threading: boolean;
  actions: Action[]=[];
}
