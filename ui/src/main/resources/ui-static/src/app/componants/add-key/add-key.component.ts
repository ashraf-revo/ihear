import {Component, OnInit} from '@angular/core';
import {BaseModel} from "../../models/base-model";
import {KeyType} from "../../models/key-type.enum";
import {Key} from "../../models/key";
import {Entry} from "../../models/entry";

@Component({
  selector: 'js-add-key',
  templateUrl: './add-key.component.html',
  styleUrls: ['./add-key.component.css']
})
export class AddKeyComponent extends BaseModel<Key> implements OnInit {
  keyTypes: KeyType[] = Object.keys(KeyType).map(it=>KeyType[it]);
  key: Key;
  showSave: boolean = false;

  constructor() {
    super()
  }

  ngOnInit() {
    console.log(this.keyTypes);
  }

  onEvent(event: string): void {
    console.log(event);
    this.onSave.emit(new Entry<Key>(this.identifer, this.key));
    this.hide();
  }

  onShow(instance: any[]): void {
    this.showSave = false;
    if (instance != null && instance.length > 0 && instance[0] != null) {
      this.key = instance[0];
    }
    if (instance == null || instance[0] == null) {
      this.showSave = true;
      this.key = new Key();
      this.key.keyType = KeyType[this.keyTypes[0]];
    }
  }
}
