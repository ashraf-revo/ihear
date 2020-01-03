import {Component, OnInit} from '@angular/core';
import {BaseModel} from "../../models/base-model";
import {ResourceType} from "../../models/resource-type.enum";
import {Entry} from "../../models/entry";
import {Resource} from "../../models/resource";

@Component({
  selector: 'js-add-resource',
  templateUrl: './add-resource.component.html',
  styleUrls: ['./add-resource.component.css']
})
export class AddResourceComponent extends BaseModel<Resource> implements OnInit {
  resourceTypes: ResourceType[] = Object.keys(ResourceType).map(it => ResourceType[it]);
  resource: Resource;
  showSave: boolean = false;

  constructor() {
    super()
  }

  ngOnInit() {
  }

  onEvent(event: string): void {
    console.log(event);
    this.onSave.emit(new Entry<Resource>(this.identifer, this.resource));
    this.hide();
  }

  onShow(instance: any[]): void {
    this.showSave = false;
    if (instance != null && instance.length > 0 && instance[0] != null) {
      this.resource = instance[0];
    }
    if (instance == null || instance[0] == null) {
      this.showSave = true;
      this.resource = new Resource();
      this.resource.resourceType = this.resourceTypes[0];
    }
  }
}
