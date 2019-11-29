import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {Schema} from '../models/schema';
import {Key} from '../models/key';
import {KeyType} from '../models/key-type.enum';
import {Listener} from '../models/listener';
import {ListenerType} from '../models/listener-type.enum';
import {Action} from '../models/action';
import {ActionType} from '../models/action-type.enum';
import {PinType} from '../models/pin-type.enum';

@Injectable({
  providedIn: 'root'
})
export class PiService {

  constructor() {
  }

  public findOne(id: string): Observable<Schema> {

    let schema = new Schema();
    schema.id = id;
    schema.meta = 'asfa';
    let key1 = new Key();
    key1.keyType = KeyType.DOWN;
    let l1 = new Listener();
    l1.listenerType = ListenerType.CLICK;
    let a11 = new Action();
    a11.actionType = ActionType.RECORD;
    a11.pinType = PinType.NONE;
    let a12 = new Action();
    a12.actionType = ActionType.SLEEP;
    a12.pinType = PinType.NONE;
    l1.actions = [a11, a12];


    let l2 = new Listener();
    l2.listenerType = ListenerType.HOVER;
    let a21 = new Action();
    a21.actionType = ActionType.RECORD;
    a21.pinType = PinType.NONE;
    let a22 = new Action();
    a22.actionType = ActionType.SLEEP;
    a22.pinType = PinType.NONE;
    l2.actions = [a21, a22];
    key1.listener = [l1, l2];

    schema.keys = [key1];
    return of(schema);
  }

  public save(schema: Schema): Observable<Schema> {
    return of(schema);
  }
}
