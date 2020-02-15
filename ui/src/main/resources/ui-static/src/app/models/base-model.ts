import {EventEmitter, ViewChild} from "@angular/core";
import {BasicUiModelComponent} from "../componants/basic-ui-model/basic-ui-model.component";
import {Entry} from "./entry";

export abstract class BaseModel<T> {
  @ViewChild(BasicUiModelComponent)
  public model: BasicUiModelComponent;
  protected _onSave: EventEmitter<Entry<T>> = new EventEmitter<Entry<T>>();
  public _identifer: any;

  hide() {
    this.model.hide()
  }


  public get onSave(): EventEmitter<Entry<T>> {
    return this._onSave;
  }

  get identifer(): any {
    return this._identifer;
  }

  abstract onEvent(event: string): void;

  abstract onShow(instance: Entry<T>): void;

  abstract show(identifer: string, data: T): void;

}
