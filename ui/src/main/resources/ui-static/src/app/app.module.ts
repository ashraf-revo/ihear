import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { JoyStickComponent } from './componants/joy-stick/joy-stick.component';
import {FormsModule} from '@angular/forms';
import {PiService} from './services/pi.service';

@NgModule({
  declarations: [
    AppComponent,
    JoyStickComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule
  ],
  providers: [PiService],
  bootstrap: [AppComponent]
})
export class AppModule { }
