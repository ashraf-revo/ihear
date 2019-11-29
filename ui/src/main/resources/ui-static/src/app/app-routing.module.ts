import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {JoyStickComponent} from './componants/joy-stick/joy-stick.component';

const routes: Routes = [
  {path: 'schema/:id', component: JoyStickComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
