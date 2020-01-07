import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SchemaComponent} from './componants/schema/schema.component';
import {HomeComponent} from "./componants/home/home.component";
import {StreamComponent} from "./componants/stream/stream.component";
import {BaseComponent} from "./componants/base/base.component";
import {PlayerComponent} from "./componants/player/player.component";

const routes: Routes = [
  {
    path: '', component: BaseComponent, children:
      [
        {path: '', component: HomeComponent},
        {path: 'home', component: HomeComponent},
        {path: 'stream', component: StreamComponent},
        {path: 'player/:id', component: PlayerComponent},
        {path: 'stream/:id', component: StreamComponent},
        {path: 'schema', component: SchemaComponent},
        {path: 'schema/:id', component: SchemaComponent}
      ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
