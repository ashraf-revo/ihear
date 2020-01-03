import {Component, OnInit} from '@angular/core';
import {BaseModel} from "../../models/base-model";
import {PiService} from "../../services/pi.service";
import {Schema} from "../../models/schema";
import {Entry} from "../../models/entry";

@Component({
  selector: 'js-search-schema',
  templateUrl: './search-schema.component.html',
  styleUrls: ['./search-schema.component.css']
})
export class SearchSchemaComponent extends BaseModel<Schema> implements OnInit {
  schemas: Schema[] = [];
  showSave: boolean = false;
  query: string = '';

  constructor(private piService: PiService) {
    super()
  }

  ngOnInit() {
  }

  onEvent(event: string): void {
  }

  onShow(instance: any[]): void {
    this.piService.findAllSchemas().subscribe(it => this.schemas = it, error => {
    })
  }

  select(schema: Schema) {
    this.onSave.emit(new Entry<Schema>(this.identifer, schema));
    this.hide();
  }
}
