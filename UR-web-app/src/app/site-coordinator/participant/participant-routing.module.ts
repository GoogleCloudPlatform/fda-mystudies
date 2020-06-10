import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {DashboardComponent} from './dashboard/dashboard.component';
import {SiteParticipantListComponent} from
  './site-participant-list/site-participant-list.component';
import {StudyParticipantListComponent} from
  './study-participant-list/study-participant-list.component';
import {AppParticipantListComponent} from
  './app-participant-list/app-participant-list.component';
import {ParticipantDetailsComponent} from
  './participant-details/participant-details.component';


const routes: Routes = [

  {path: 'dashboard', component: DashboardComponent},
  {
    path: 'site/:siteId',
    component: SiteParticipantListComponent,
  },
  {
    path: 'study/:studyId',
    component: StudyParticipantListComponent,
  },
  {
    path: 'app/:appId',
    component: AppParticipantListComponent,
  },
  {
    path: ':partcipantId',
    component: ParticipantDetailsComponent,
  },
  {path: '', redirectTo: 'dashboard',
    pathMatch: 'full'},

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ParticipantRoutingModule { }
