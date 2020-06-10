import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ParticipantRoutingModule} from './participant-routing.module';
import {AppParticipantListComponent} from
  './app-participant-list/app-participant-list.component';
import {SiteParticipantListComponent} from
  './site-participant-list/site-participant-list.component';
import {StudyParticipantListComponent} from
  './study-participant-list/study-participant-list.component';
import {ParticipantDetailsComponent} from
  './participant-details/participant-details.component';
import {DashboardComponent} from
  './dashboard/dashboard.component';

@NgModule({
  declarations: [AppParticipantListComponent, SiteParticipantListComponent,
    StudyParticipantListComponent, ParticipantDetailsComponent,
    DashboardComponent],
  imports: [
    CommonModule,
    ParticipantRoutingModule,
  ],
  providers: [],
})
export class ParticipantModule { }
