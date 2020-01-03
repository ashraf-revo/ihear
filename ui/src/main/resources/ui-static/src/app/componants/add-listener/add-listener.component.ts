import {Component, OnInit} from '@angular/core';
import {BaseModel} from "../../models/base-model";
import {Entry} from "../../models/entry";
import {ListenerType} from "../../models/listener-type.enum";
import {Listener} from "../../models/listener";

@Component({
  selector: 'js-add-listener',
  templateUrl: './add-listener.component.html',
  styleUrls: ['./add-listener.component.css']
})
export class AddListenerComponent extends BaseModel<Listener> implements OnInit {
  listenerTypes: ListenerType[] = Object.keys(ListenerType).map(it=>ListenerType[it]);
  listener: Listener;
  showSave: boolean = false;

  constructor() {
    super()
  }

  ngOnInit() {
  }

  onEvent(event: string): void {
    console.log(event);
    this.onSave.emit(new Entry<Listener>(this.identifer, this.listener));
    this.hide();
  }

  onShow(instance: any[]): void {
    this.showSave = false;
    if (instance != null && instance.length > 0 && instance[0] != null) {
      this.listener = instance[0];
    }
    if (instance == null || instance[0] == null) {
      this.showSave = true;
      this.listener = new Listener();
      this.listener.threading=false;
      this.listener.listenerType=ListenerType[this.listenerTypes[0]];
    }
  }
}
