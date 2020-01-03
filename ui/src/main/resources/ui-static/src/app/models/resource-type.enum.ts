import {ActionType} from "./action-type.enum";

export enum ResourceType {
  PIN_1 = "PIN_1", CAMERA = "CAMERA", THREAD = "THREAD"
}

 function getResourceTypeKind(resourceType: ResourceType): string {
  return resourceType.toString().replace(/_.*/, "");
}

export function getAllowedActionsOnResourceType(resourceType: ResourceType): ActionType[] {
  let kind = getResourceTypeKind(resourceType);
  switch (kind) {
    case "PIN":
      return [ActionType.ON, ActionType.OFF, ActionType.BLINK, ActionType.TOGGLE];
    case "CAMERA":
      return [ActionType.RECORD, ActionType.TEARDOWN];
    case "THREAD":
      return [ActionType.SLEEP];
    default:
      return []
  }
}
