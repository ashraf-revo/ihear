import {Component, HostListener, OnInit} from '@angular/core';
import {Device} from "../../models/device";
import {Schema} from "../../models/schema";
import {KeyType} from "../../models/key-type.enum";
import {from, Observable} from "rxjs";
import {filter, map, mergeMap} from "rxjs/operators";
import {WsService} from "../../services/ws.service";
import {ActivatedRoute} from "@angular/router";
import {PiService} from "../../services/pi.service";
import {ActionType} from "../../models/action-type.enum";
import {ResourceType} from "../../models/resource-type.enum";

@Component({
  selector: 'js-joystick',
  templateUrl: './joystick.component.html',
  styleUrls: ['./joystick.component.css']
})
export class JoystickComponent implements OnInit {

  private device: Device;
  private schema: Schema;
  private pc: RTCPeerConnection;
  private config = {
    sdpSemantics: 'unified-plan',
    iceServers: [{urls: ['stun:stun.l.google.com:19302']}]
  };

  private keyMaps: { [key: string]: KeyType } = {
    "ArrowUp": KeyType.UP,
    "ArrowDown": KeyType.DOWN,
    "ArrowRight": KeyType.RIGHT,
    "ArrowLeft": KeyType.LEFT,
    "KeyY": KeyType.Y,
    "KeyX": KeyType.X,
    "KeyB": KeyType.B,
    "KeyA": KeyType.A
  };


  constructor(private piService: PiService, private activatedRoute: ActivatedRoute, private wsService: WsService) {
  }

  ngOnInit() {
    this.wsService.listen.pipe(filter(it => it["action"] == "STREAM"))
      .subscribe(it => {
        this.setRemoteDescription((<RTCSessionDescriptionInit>{"sdp": it["sdp"], "type": it["type"]}));
      });

    this.activatedRoute.params.pipe(map(it => it['id']),
      mergeMap(it => this.piService.findDevice(it)), map(it => {
        this.device = it;
        return it;
      }), mergeMap(it => this.piService.findSchema(it.schemaId))).subscribe(it => {
      this.schema = it;
    })
  }


  @HostListener('window:keyup', ['$event'])
  keyEvent(event: KeyboardEvent) {
    let key = this.keyMaps[event.code];
    if (key) {
      this.schema.keys.filter(it => it.keyType == key).forEach(it => {
        this.notify(it).subscribe();
      })
    }
  }

  private notify(payload: any): Observable<Object> {
    return this.piService.notify(this.device.id, {
      "payload": payload,
      "to": this.device.clientId + "/" + this.device.id
    })
  }


  stream() {
    this.pc = new RTCPeerConnection(this.config);
    this.pc.addEventListener('track', (evt) => {
      if (evt.track.kind == 'video') {
        (<HTMLVideoElement>document.getElementById('video')).srcObject = evt.streams[0];
      } else {
        (<HTMLAudioElement>document.getElementById('audio')).srcObject = evt.streams[0];
      }
    });
    this.pc.addTransceiver('video', {direction: 'recvonly'});
    this.pc.addTransceiver('audio', {direction: 'recvonly'});
    from(this.pc.createOffer()).pipe(mergeMap(offer => {
        return this.pc.setLocalDescription(offer);
      }),
      mergeMap(() => new Promise((resolve) => {
        if (this.pc.iceGatheringState === 'complete') {
          resolve();
        } else {
          let checkState = () => {
            if (this.pc.iceGatheringState === 'complete') {
              this.pc.removeEventListener('icegatheringstatechange', checkState);
              resolve();
            }
          };
          this.pc.addEventListener('icegatheringstatechange', checkState);
        }
      }))).pipe(mergeMap(() => {
      return this.notify(this.generateStreamMessage());
    })).subscribe();
  }

  generateStreamMessage(): any {
    return {
      "keyType": "SPACE",
      actions: [{
        "actionType": ActionType.STREAM,
        "resourceType": ResourceType.CAMERA,
        "data": {"sdp": this.pc.localDescription.sdp, "type": this.pc.localDescription.type}
      }]
    };
  }

  generateTeardownMessage(): any {
    return {
      "keyType": "SPACE",
      actions: [{
        "actionType": ActionType.TEARDOWN,
        "resourceType": ResourceType.CAMERA,
        "data": {}
      }]
    };
  }

  setRemoteDescription(remoteDescription: RTCSessionDescriptionInit): Promise<void> {
    return this.pc.setRemoteDescription(remoteDescription)
  }

  teardown() {
    this.notify(this.generateTeardownMessage()).subscribe(it => {
    }, error => {
    }, () => {
      if (this.pc) {
        this.pc.close();
      }
    })
  }
}
