import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {ManageSitesRoutingModule} from './manage-sites-routing.module';
import {ManageSitesService} from './shared/manage-sites.service';
import {AppParticipantListComponent} from
  './app-participant-list/app-participant-list.component';
import {SiteParticipantListComponent} from
  './site-participant-list/site-participant-list.component';
import {StudyParticipantListComponent} from
  './study-participant-list/study-participant-list.component';
import {ParticipantDetailComponent} from
  './participant-detail/participant-detail.component';
import {DashboardComponent} from
  './dashboard/dashboard.component';

@NgModule({
  declarations: [AppParticipantListComponent, SiteParticipantListComponent,
    StudyParticipantListComponent, ParticipantDetailComponent,
    DashboardComponent],
  imports: [
    CommonModule,
    ManageSitesRoutingModule,
  ],
  providers: [ManageSitesService],
})
export class ManageSitesModule { }
