import {Component, ComponentFactoryResolver, OnInit, Type, ViewContainerRef} from '@angular/core';
import {SearchSchemaComponent} from "../search-schema/search-schema.component";
import {PiService} from "../../services/pi.service";
import {BaseDevice, Device} from "../../models/device";
import {ActivatedRoute, Params} from "@angular/router";
import {filter, map, mergeMap} from "rxjs/operators";
import {DeviceType} from "../../models/device-type.enum";
import {Util} from "../../services/util";
import {Entry} from "../../models/entry";
import {Schema} from "../../models/schema";
import {PerfectScrollbarConfigInterface} from "ngx-perfect-scrollbar";

@Component({
  selector: 'js-device',
  templateUrl: './device.component.html',
  styleUrls: ['./device.component.css'],
  entryComponents: [SearchSchemaComponent]
})
export class DeviceComponent implements OnInit {
  device: Device = new Device();
  devices: Device[] = [];
  id: string = null;
  searchSchemaComponent: SearchSchemaComponent;
  deviceTypes: string[] = Object.values(DeviceType);
  config: PerfectScrollbarConfigInterface = {
  };


  constructor(private piService: PiService, private viewContainerRef: ViewContainerRef, private componentFactoryResolver: ComponentFactoryResolver, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    this.load();
    this.device.deviceType = (<any>DeviceType)[this.deviceTypes[0]];
    this.searchSchemaComponent.onSave.subscribe(it => {
      this.device.schemaId = it.data.id
    });
    this.piService.findAllDevices().subscribe(it => {
      this.devices = it;
    });
    this.activatedRoute.params.pipe(map((it: Params) => it['id']), filter(it => it),
      mergeMap(it => this.piService.findDevice(it))).subscribe(it => {
      this.id = it.id;
      this.device = it
    }, error => {
    });
  }

  private getComponant<T>(component: Type<T>): T {
    return this.viewContainerRef.createComponent(this.componentFactoryResolver.resolveComponentFactory(component)).instance;
  }

  save() {
    console.log(window.location);
    this.piService.saveDevice(this.device).subscribe(it => {
        if (this.id == null) {
          this.devices.push(it);
          Util.download("ihear.key", JSON.stringify(BaseDevice.of(it)));
          this.device = new Device();
          this.device.deviceType = (<any>DeviceType)[this.deviceTypes[0]];
        } else {
          this.device = it;
          this.devices[this.devices.findIndex(st => st.id == it.id)] = it;
        }
      }
      , error => {
      }, () => {
      })
  }


  private load() {
    this.searchSchemaComponent = this.getComponant(SearchSchemaComponent);
  }

}

