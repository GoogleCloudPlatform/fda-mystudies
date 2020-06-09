import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {DashboardComponent} from './dashboard/dashboard.component';
import {SiteParticipantListComponent} from
  './site-participant-list/site-participant-list.component';
import {StudyParticipantListComponent} from
  './study-participant-list/study-participant-list.component';
import {AppParticipantListComponent} from
  './app-participant-list/app-participant-list.component';
import {ParticipantDetailComponent} from
  './participant-detail/participant-detail.component';


const routes: Routes = [

  {path: 'dashboard', component: DashboardComponent},
  {
    path: 'site-participant-list/:siteId',
    component: SiteParticipantListComponent,
  },
  {
    path: 'study-participant-list/:studyId',
    component: StudyParticipantListComponent,
  },
  {
    path: 'app-participant-list/:appId',
    component: AppParticipantListComponent,
  },
  {
    path: 'participant-detail/:partcipantId',
    component: ParticipantDetailComponent,
  },
  {path: '', redirectTo: 'dashboard',
    pathMatch: 'full'},

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ManageSitesRoutingModule { }
