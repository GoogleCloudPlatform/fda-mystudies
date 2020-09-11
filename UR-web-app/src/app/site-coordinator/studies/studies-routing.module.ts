import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {StudyListComponent} from './study-list/study-list.component';
import {StudyDetailsComponent} from './study-details/study-details.component';
import {SitesModule} from '../sites/sites.module';

const routes: Routes = [
  {
    path: 'sites',
    loadChildren: async (): Promise<SitesModule> =>
      import('../sites/sites.module').then((m) => m.SitesModule),
  },
  {
    path: '',
    component: StudyListComponent,
  },
  {
    path: ':studyId',
    component: StudyDetailsComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class StudiesRoutingModule {}
