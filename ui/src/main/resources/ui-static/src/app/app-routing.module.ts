import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SchemaComponent} from './componants/schema/schema.component';
import {HomeComponent} from "./componants/home/home.component";
import {BaseComponent} from "./componants/base/base.component";
import {DeviceComponent} from "./componants/device/device.component";
import {BaseHomeComponent} from "./componants/base-home/base-home.component";
import {JoystickComponent} from "./componants/joystick/joystick.component";
import {DashboardComponent} from "./componants/dashboard/dashboard.component";
import {ErrorComponent} from "./componants/error/error.component";

const routes: Routes = [
  {
    path: '', component: BaseComponent, children:
      [{path: '', component: DashboardComponent},
        {
          path: 'home', component: BaseHomeComponent, children: [
            {path: '', component: HomeComponent},
            {path: 'schema', component: SchemaComponent},
            {path: 'schema/:id', component: SchemaComponent},
            {path: 'device', component: DeviceComponent},
            {path: 'device/:id', component: DeviceComponent},
            {path: 'device/:id/joystick', component: JoystickComponent},
          ]
        },
        {path: '403', component: ErrorComponent},
        {path: 'error', component: ErrorComponent},
        {path: '**', component: ErrorComponent}
      ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
