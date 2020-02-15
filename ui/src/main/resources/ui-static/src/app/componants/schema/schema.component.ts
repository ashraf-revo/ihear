import {Component, ComponentFactoryResolver, OnInit, Type, ViewContainerRef} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';
import {Schema} from '../../models/schema';
import {PiService} from '../../services/pi.service';
import {filter, map, mergeMap} from 'rxjs/operators';
import {AddKeyComponent} from "../add-key/add-key.component";
import {AddActionComponent} from "../add-action/add-action.component";
import {Resource} from "../../models/resource";

@Component({
  selector: 'js-schema',
  templateUrl: './schema.component.html',
  styleUrls: ['./schema.component.css'],
  entryComponents: [ AddKeyComponent, AddActionComponent]
})
export class SchemaComponent implements OnInit {
  schema: Schema = new Schema();
  addKeyComponent: AddKeyComponent;
  addActionComponent: AddActionComponent;

  constructor(private piService: PiService, private activatedRoute: ActivatedRoute,
              private viewContainerRef: ViewContainerRef, private componentFactoryResolver: ComponentFactoryResolver) {
  }

  ngOnInit() {
    this.load();
    this.addKeyComponent.onSave.subscribe(it => {
      if (!this.schema.keys) this.schema.keys = [];
      this.schema.keys.push(it.data)
    });
    this.addActionComponent.onSave.subscribe(it => {
      if (!this.schema.keys) this.schema.keys = [];
      if (this.schema.keys.length > 0) {
        if (!this.schema.keys[it.identifer].actions) {
          this.schema.keys[it.identifer].actions = [];
        }
        this.schema.keys[it.identifer].actions.push(it.data)
      }
    });
    this.activatedRoute.params.pipe(map((it: Params) => it['id']), filter(it => it),
      mergeMap(it => this.piService.findSchema(it))).subscribe(it => this.schema = it, error => {
    });
  }


  private load() {
    this.addKeyComponent = this.getComponant(AddKeyComponent);
    this.addActionComponent = this.getComponant(AddActionComponent);
  }

  delete(array: any[], index: number): boolean {
    if (index == -1) {
      let length = array.length;
      let d: number = 0;
      for (let i = 0; i < length; i++) {
        if (!this.delete(array, d)) {
          d++;
        }
      }

    } else {
      if (array[index] instanceof Resource) {
        // let actions1: Action[] = [].concat.apply([], [].concat.apply([], this.schema.event.keys.map(it => it.listeners))
        //   .map(it => it.actions)).filter(it => it.resourceType == array[index].resourceType);
        // let actions2: Action[] = [].concat.apply([], [].concat.apply([], this.schema.event.listeners)
        //   .map(it => it.actions)).filter(it => it.resourceType == array[index].resourceType);
        // if (actions1.length > 0 || actions2.length > 0) {
        //   console.log("iam sorry but it seems you use this resources in some action");
        //   return false;
        // }
      }
      array.splice(index, 1);
      return true;
    }
  }

  save() {
    this.piService.saveSchema(this.schema).subscribe(it => this.schema = it);
  }

  private getComponant<T>(component: Type<T>): T {
    return this.viewContainerRef.createComponent(this.componentFactoryResolver.resolveComponentFactory(component)).instance;
  }
}
