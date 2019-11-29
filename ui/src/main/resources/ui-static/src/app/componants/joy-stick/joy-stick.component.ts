import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';
import {Schema} from '../../models/schema';
import {PiService} from '../../services/pi.service';
import {map, mergeMap} from 'rxjs/operators';
import {ActionType} from '../../models/action-type.enum';
import {KeyType} from '../../models/key-type.enum';
import {ListenerType} from '../../models/listener-type.enum';
import {PinType} from '../../models/pin-type.enum';

@Component({
  selector: 'js-joy-stick',
  templateUrl: './joy-stick.component.html',
  styleUrls: ['./joy-stick.component.css']
})
export class JoyStickComponent implements OnInit {
  schema: Schema;
  keys: string[] = Object.keys(KeyType);
  listeners: string[] = Object.keys(ListenerType);
  actions: string[] = Object.keys(ActionType);
  pins: string[] = Object.keys(PinType);

  constructor(private piService: PiService, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    this.activatedRoute.params.pipe(map((it: Params) => it['id']), mergeMap(it => this.piService.findOne(it))).subscribe(it => this.schema = it);
    console.log(this.keys);
    console.log(this.listeners);
    console.log(this.actions);
    console.log(this.pins);
  }

  delete(array: any[], index: number) {
    if (index == -1) {
      array.splice(0, array.length);
    } else {
      array.splice(index, 1);
    }
  }

  save() {
    this.piService.save(this.schema).subscribe(it => this.schema = it);
  }
}
