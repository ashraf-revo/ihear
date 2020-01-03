import {EventEmitter, ViewChild} from "@angular/core";
import {BasicUiModelComponent} from "../componants/basic-ui-model/basic-ui-model.component";
import {Entry} from "./entry";

export abstract class BaseModel<T> {
  @ViewChild(BasicUiModelComponent)
  private model: BasicUiModelComponent;
  protected _onSave: EventEmitter<Entry<T>> = new EventEmitter<Entry<T>>();
  private _identifer: any;

  hide() {
    this.model.hide()
  }

  show(identifer: any, instance: any[]) {
    this._identifer = identifer;
    this.onShow(instance);
    this.model.show()
  }

  public get onSave(): EventEmitter<Entry<T>> {
    return this._onSave;
  }

  get identifer(): any {
    return this._identifer;
  }

  abstract onEvent(event: string): void;

  abstract onShow(instance: any[]): void;

}
