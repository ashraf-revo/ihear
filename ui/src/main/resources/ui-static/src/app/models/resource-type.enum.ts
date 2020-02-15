import {ActionType} from "./action-type.enum";

export enum ResourceType {
  PIN = "PIN", CAMERA = "CAMERA", THREAD = "THREAD", SOCKET = "SOCKET"
}

export function getAllowedActionsOnResourceType(resourceType: ResourceType): ActionType[] {
  switch (resourceType) {
    case "PIN":
      return [ActionType.ON, ActionType.OFF, ActionType.BLINK, ActionType.TOGGLE];
    case "CAMERA":
      return [ActionType.SHOT, ActionType.RECORD];
    case "THREAD":
      return [ActionType.SLEEP];
    case "SOCKET":
      return [ActionType.ACK, ActionType.NAK];
    default:
      return []
  }
}

export function getAllowedOptionsOnResourceType(resourceType: ResourceType, actionType: ActionType): Map<string, string[]> {
  switch (resourceType) {
    case "PIN":
      let pin = new Map<string, any>();
      pin.set("Pin", ["1", "2", "3"]);
      return pin;
    case "CAMERA":
      return new Map<string, string[]>();
    case "THREAD":
      let thread = new Map<string, string[]>();
      if (actionType == ActionType.SLEEP)
        thread.set("Time", ["200", "500", "1000", "2000", "5000"]);
      thread.set("Delay", ["200", "500", "1000", "2000", "5000"]);
      return thread;
    case "SOCKET":
      let socket = new Map<string, any>();
      if (actionType == ActionType.ACK)
        socket.set("health", ["*", "mem", "cpu"]);
      return socket;
    default:
      return new Map<string, any>()
  }
}
