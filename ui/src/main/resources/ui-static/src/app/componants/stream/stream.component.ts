import {Component, ComponentFactoryResolver, OnInit, Type, ViewContainerRef} from '@angular/core';
import {SearchSchemaComponent} from "../search-schema/search-schema.component";
import {PiService} from "../../services/pi.service";
import {Stream} from "../../models/stream";
import {ActivatedRoute, Params} from "@angular/router";
import {filter, map, mergeMap} from "rxjs/operators";

@Component({
  selector: 'js-stream',
  templateUrl: './stream.component.html',
  styleUrls: ['./stream.component.css'],
  entryComponents: [SearchSchemaComponent]

})
export class StreamComponent implements OnInit {
  stream: Stream = new Stream();
  streams: Stream[] = [];
  id: string = null;
  searchSchemaComponent: SearchSchemaComponent;

  constructor(private piService: PiService, private viewContainerRef: ViewContainerRef, private componentFactoryResolver: ComponentFactoryResolver, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    this.load();
    this.searchSchemaComponent.onSave.subscribe(it => {
      this.stream.schemaId = it.data.id
    });
    this.piService.findAllStreams().subscribe(it => {
      this.streams = it;
    });
    this.activatedRoute.params.pipe(map((it: Params) => it['id']), filter(it => it),
      mergeMap(it => this.piService.findStream(it))).subscribe(it => {
      this.id = it.id;
      this.stream = it
    }, error => {
    });
  }

  private getComponant<T>(component: Type<T>): T {
    return this.viewContainerRef.createComponent(this.componentFactoryResolver.resolveComponentFactory(component)).instance;
  }

  save() {
    this.piService.saveStream(this.stream).subscribe(it => {
        if (this.id == null) {
          this.streams.push(it);
          this.stream=new Stream();
        } else {
          this.stream = it;
          this.streams[this.streams.findIndex(st => st.id == it.id)] = it;
        }
      }
      , error => {
      }, () => {
      })
  }

  showSelect() {
    this.searchSchemaComponent.show(undefined, []);
  }

  private load() {
    this.searchSchemaComponent = this.getComponant(SearchSchemaComponent);
  }
}
