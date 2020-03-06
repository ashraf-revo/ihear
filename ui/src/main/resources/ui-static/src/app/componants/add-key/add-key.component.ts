import {Component, OnInit} from '@angular/core';
import {BaseModel} from "../../models/base-model";
import {KeyType} from "../../models/key-type.enum";
import {Key} from "../../models/key";
import {Entry} from "../../models/entry";
import {KeyEvent} from "../../models/key-event";

@Component({
  selector: 'js-add-key',
  templateUrl: './add-key.component.html',
  styleUrls: ['./add-key.component.css']
})
export class AddKeyComponent extends BaseModel<Key> implements OnInit {
  keyTypes: KeyType[] = Object.keys(KeyType).map(it => KeyType[it]);
  keyEvents: KeyEvent[] = Object.keys(KeyEvent).map(it => KeyEvent[it]);
  key: Key;
  showSave: boolean = false;

  constructor() {
    super()
  }

  ngOnInit() {
  }

  onEvent(event: string): void {
    console.log(event);
    this.onSave.emit(new Entry<Key>(this.identifer, this.key));
    this.hide();
  }

  onShow(entry: Entry<Key>): void {
    this._identifer = entry.identifer;
    if (entry.data) {
      this.key = entry.data;
      this.showSave = false;
    } else {
      this.key = new Key();
      if (this.keyTypes.length > 0)
        this.key.keyType = this.keyTypes[0];
      if (this.keyEvents.length > 0)
        this.key.keyEvent = this.keyEvents[0];
      this.showSave = true;
    }


    // this.showSave = false;
    // if (instance != null && instance.length > 0 && instance[0] != null) {
    //   this.key = instance[0];
    // }
    // if (instance == null || instance[0] == null) {
    //   this.showSave = true;
    //   this.key = new Key();
    //   this.key.keyType = KeyType[this.keyTypes[0]];
    // }


  }

  show(identifer: string, data: Key) {
    this.onShow(new Entry<Key>(identifer, data));
    this.model.show()
  }
}
