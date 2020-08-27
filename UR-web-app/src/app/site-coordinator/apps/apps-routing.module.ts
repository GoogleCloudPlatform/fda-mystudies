import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {AppListComponent} from './app-list/app-list.component';
import {AppDetailsComponent} from './app-details/app-details.component';

const routes: Routes = [
  {
    path: '',
    component: AppListComponent,
  },
  {
    path: ':appId',
    component: AppDetailsComponent,
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AppsRoutingModule {}
