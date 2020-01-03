import {Component, ComponentFactoryResolver, OnInit, Type, ViewContainerRef} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';
import {Schema} from '../../models/schema';
import {PiService} from '../../services/pi.service';
import {filter, map, mergeMap} from 'rxjs/operators';
import {AddKeyComponent} from "../add-key/add-key.component";
import {AddListenerComponent} from "../add-listener/add-listener.component";
import {AddActionComponent} from "../add-action/add-action.component";
import {AddResourceComponent} from "../add-resource/add-resource.component";
import {Resource} from "../../models/resource";
import {Action} from "../../models/action";

@Component({
  selector: 'js-schema',
  templateUrl: './schema.component.html',
  styleUrls: ['./schema.component.css'],
  entryComponents: [AddResourceComponent, AddKeyComponent, AddListenerComponent, AddActionComponent]
})
export class SchemaComponent implements OnInit {
  schema: Schema = new Schema();
  addResourceComponent: AddResourceComponent;
  addKeyComponent: AddKeyComponent;
  addListenerComponent: AddListenerComponent;
  addActionComponent: AddActionComponent;

  constructor(private piService: PiService, private activatedRoute: ActivatedRoute,
              private viewContainerRef: ViewContainerRef, private componentFactoryResolver: ComponentFactoryResolver) {
  }

  ngOnInit() {
    this.load();
    this.addResourceComponent.onSave.subscribe(it => {
      if (this.schema.event.resources.filter(itp => itp.resourceType == it.data.resourceType).length == 0)
        this.schema.event.resources.push(it.data);
      else {
        console.log("sorry " + it.data.resourceType + " aready exist")
      }
    });
    this.addKeyComponent.onSave.subscribe(it => {
      this.schema.event.keys.push(it.data)
    });
    this.addListenerComponent.onSave.subscribe(it => {
      if (it.identifer == -1) {
        this.schema.event.listeners.push(it.data)
      } else {
        this.schema.event.keys[it.identifer].listeners.push(it.data)
      }
    });
    this.addActionComponent.onSave.subscribe(it => {
      if (it.identifer.identifer1 == -1) {
        this.schema.event.listeners[it.identifer.identifer2].actions.push(it.data);
      } else {
        this.schema.event.keys[it.identifer.identifer1].listeners[it.identifer.identifer2].actions.push(it.data);
      }
    });

    this.activatedRoute.params.pipe(map((it: Params) => it['id']), filter(it => it),
      mergeMap(it => this.piService.findSchema(it))).subscribe(it => this.schema = it, error => {
    });
  }


  private load() {
    this.addResourceComponent = this.getComponant(AddResourceComponent);
    this.addKeyComponent = this.getComponant(AddKeyComponent);
    this.addListenerComponent = this.getComponant(AddListenerComponent);
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
        let actions1: Action[] = [].concat.apply([], [].concat.apply([], this.schema.event.keys.map(it => it.listeners))
          .map(it => it.actions)).filter(it => it.resourceType == array[index].resourceType);
        let actions2: Action[] = [].concat.apply([], [].concat.apply([], this.schema.event.listeners)
          .map(it => it.actions)).filter(it => it.resourceType == array[index].resourceType);
        if (actions1.length > 0 || actions2.length > 0) {
          console.log("iam sorry but it seems you use this resources in some action");
          return false;
        }
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
