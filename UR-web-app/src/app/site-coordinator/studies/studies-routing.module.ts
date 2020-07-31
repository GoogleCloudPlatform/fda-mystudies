import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {StudyListComponent} from './study-list/study-list.component';

const routes: Routes = [
  {
    path: '',
    component: StudyListComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class StudiesRoutingModule {}
