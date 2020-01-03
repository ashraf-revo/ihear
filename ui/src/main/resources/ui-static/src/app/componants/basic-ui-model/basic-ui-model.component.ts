import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'js-basic-ui-model',
  templateUrl: './basic-ui-model.component.html',
  styleUrls: ['./basic-ui-model.component.css']
})
export class BasicUiModelComponent implements OnInit {
  public visible = false;
  public visibleAnimate = false;
  @Output()
  public events: EventEmitter<string> = new EventEmitter<string>();
  @Input()
  public title: string;
  @Input()
  public showSave: boolean;

  constructor() {
  }

  ngOnInit() {
  }


  public show(): void {
    this.visible = true;
    setTimeout(() => this.visibleAnimate = true, 100);
  }

  public hide(): void {
    this.visibleAnimate = false;
    setTimeout(() => this.visible = false, 300);
  }

  save() {
    this.events.emit("close");
  }

  public onContainerClicked(event: MouseEvent): void {
    if ((<HTMLElement>event.target).classList.contains('modal')) {
      this.hide();
    }
  }

}
