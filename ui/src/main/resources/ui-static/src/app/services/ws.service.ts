import {Injectable} from '@angular/core';
import {AuthService} from "./auth.service";
import {AuthUser} from "../models/auth-user";
import {filter} from 'rxjs/operators';
import {Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WsService {
  private webSocket: WebSocket;
  private _connected: boolean = false;
  private _messages: Subject<any> = new Subject<any>();


  constructor(private authService: AuthService) {

    this.authService.onChange().pipe(filter(it => it.isAuth == "true")).subscribe(it => {
      this.connect(it);
    });
  }

  connect(authUser: AuthUser) {
    if (this._connected) return;
    let url = (location.protocol == "http:" ? "ws:" : "wss:") + ("//" + location.host + "/echo/" + authUser.user.id + "/-");
    this.webSocket = new WebSocket(url);
    this.webSocket.onopen = () => {
      this._connected = true;
    };
    this.webSocket.onclose = () => {
      this._connected = false;
    };
    this.webSocket.onmessage = (message) => {
      this._messages.next(JSON.parse(message.data));
    }
  }

  get listen(): Subject<any> {
    return this._messages;
  }

  get connected(): boolean {
    return this._connected;
  }
}
