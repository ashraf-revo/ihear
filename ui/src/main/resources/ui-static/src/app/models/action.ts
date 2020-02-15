import {ActionType} from './action-type.enum';
import {ResourceType} from "./resource-type.enum";

export class Action {
  actionType: ActionType;
  resourceType: ResourceType;
  data:Map<string,string>=new Map<string, string>();
}
