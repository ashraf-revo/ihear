import {Pipe, PipeTransform} from '@angular/core';
import {Action} from "../models/action";

@Pipe({
  name: 'resourceName',
  pure: false
})
export class ResourceNamePipe implements PipeTransform {

  transform(value: Action, args?: any): any {
    return value.actionType;
  }

}
