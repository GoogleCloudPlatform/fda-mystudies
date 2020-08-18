import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {AppListComponent} from './app-list/app-list.component';

const routes: Routes = [
  {
    path: '',
    component: AppListComponent,
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AppsRoutingModule {}
