import {Component, OnInit} from '@angular/core';
import {BaseModel} from "../../models/base-model";
import {Entry} from "../../models/entry";
import {ActionType} from "../../models/action-type.enum";
import {Action} from "../../models/action";
import {Resource} from "../../models/resource";
import {
  getAllowedActionsOnResourceType,
  getAllowedOptionsOnResourceType,
  ResourceType
} from "../../models/resource-type.enum";

@Component({
  selector: 'js-add-action',
  templateUrl: './add-action.component.html',
  styleUrls: ['./add-action.component.css']
})
export class AddActionComponent extends BaseModel<Action> implements OnInit {
  actionTypes: ActionType[] = [];
  resources: Resource[] = [];
  resourceTypes: ResourceType[] = Object.keys(ResourceType).map(it => ResourceType[it]);

  action: Action;
  showSave: boolean = false;
  options: Map<string, string[]> = new Map<string, string[]>();

  constructor() {
    super()
  }

  ngOnInit() {
  }

  onEvent(event: string): void {
    console.log(event);
    this.onSave.emit(new Entry<Action>(this.identifer, this.action));
    this.hide();
  }

  onShow(entry: Entry<Action>): void {
    this._identifer = entry.identifer;
    if (entry.data) {
      this.action = entry.data;
      this.showSave = false;
    } else {
      this.action = new Action();
      if (this.resourceTypes.length > 0)
        this.action.resourceType = this.resourceTypes[0];
      this.showSave = true;
    }
    this.onChange();
  }

  onChange() {
    this.actionTypes = getAllowedActionsOnResourceType(this.action.resourceType);
    if (this.actionTypes.indexOf(this.action.actionType) == -1) {
      this.action.actionType = this.actionTypes[0];
      this.action.data = new Map<string, string>();
    }
    this.options = getAllowedOptionsOnResourceType(this.action.resourceType, this.action.actionType);
    this.options.forEach((value, key) => {
      if (value.length > 0&&!this.action.data[key]) this.action.data[key] = value[0];
    });
  }

  show(iKey: string, data: Action) {
    this.onShow(new Entry<Action>(iKey, data));
    this.model.show()
  }
}
