import {Component, OnInit} from '@angular/core';
import {BaseModel} from "../../models/base-model";
import {Entry} from "../../models/entry";
import {ActionType} from "../../models/action-type.enum";
import {Action} from "../../models/action";
import {Resource} from "../../models/resource";
import {getAllowedActionsOnResourceType} from "../../models/resource-type.enum";

@Component({
  selector: 'js-add-action',
  templateUrl: './add-action.component.html',
  styleUrls: ['./add-action.component.css']
})
export class AddActionComponent extends BaseModel<Action> implements OnInit {
  actionTypes: ActionType[] = [];
  resources: Resource[] = [];
  action: Action;
  showSave: boolean = false;

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

  onShow(instance: any[]): void {
    this.showSave = false;
    if (instance != null && instance.length > 0 && instance[0] != null) {
      this.action = instance[0];
      this.onChange();
    }
    if (instance != null && instance.length > 1 && instance[1] != null) {
      this.resources = (<Resource[]>instance[1]);
    }
    if (instance == null || instance[0] == null) {
      this.showSave = true;
      this.action = new Action();

      if (this.resources.length > 0) {
        this.action.resourceType = this.resources[0].resourceType;
        this.onChange();
      }
    }
  }

  onChange() {
    this.actionTypes = getAllowedActionsOnResourceType(this.action.resourceType);
    if (!(this.action.actionType in this.actionTypes)){
      this.action.actionType=this.actionTypes[0]
    }
  }
}
