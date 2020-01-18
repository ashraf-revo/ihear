import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {SchemaComponent} from './componants/schema/schema.component';
import {FormsModule} from '@angular/forms';
import {PiService} from './services/pi.service';
import {AddKeyComponent} from './componants/add-key/add-key.component';
import {AddListenerComponent} from './componants/add-listener/add-listener.component';
import {AddActionComponent} from './componants/add-action/add-action.component';
import {BasicUiModelComponent} from './componants/basic-ui-model/basic-ui-model.component';
import {HomeComponent} from './componants/home/home.component';
import {AddResourceComponent} from './componants/add-resource/add-resource.component';
import {HttpClientModule} from "@angular/common/http";
import {SearchSchemaComponent} from "./componants/search-schema/search-schema.component";
import {StreamComponent} from './componants/stream/stream.component';
import {BaseComponent} from './componants/base/base.component';
import {SearchPipe} from './services/search.pipe';
import { PlayerComponent } from './componants/player/player.component';
import { DeviceComponent } from './componants/device/device.component';
import { BaseHomeComponent } from './componants/base-home/base-home.component';
import {WsService} from "./services/ws.service";

@NgModule({
  declarations: [
    AppComponent,
    SchemaComponent,
    AddKeyComponent,
    AddListenerComponent,
    AddActionComponent,
    BasicUiModelComponent,
    HomeComponent,
    AddResourceComponent,
    SearchSchemaComponent,
    StreamComponent,
    BaseComponent,
    SearchPipe,
    PlayerComponent,
    DeviceComponent,
    BaseHomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [PiService,WsService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
