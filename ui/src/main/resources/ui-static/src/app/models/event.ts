import {Key} from "./key";
import {Listener} from "./listener";
import {Resource} from "./resource";

export class Event {
  resources: Resource[] = [];
  listeners: Listener[] = [];
  keys: Key[] = [];
}
