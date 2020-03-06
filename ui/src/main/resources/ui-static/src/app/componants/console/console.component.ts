import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Device} from "../../models/device";
import {PerfectScrollbarComponent, PerfectScrollbarConfigInterface} from "ngx-perfect-scrollbar";

@Component({
  selector: 'js-console',
  templateUrl: './console.component.html',
  styleUrls: ['./console.component.css']
})
export class ConsoleComponent implements OnInit {
  config: PerfectScrollbarConfigInterface = {};
  @Input()
  public device: Device;
  public events: any[] = ["messa1", "messa2"];

  @ViewChild(PerfectScrollbarComponent, {static: false})
  componentRef: PerfectScrollbarComponent;

  constructor() {
  }

  ngOnInit() {
  }

  public push(e: any) {
    this.events.push(e);
    this.componentRef.directiveRef.scrollToBottom();
  }
}
