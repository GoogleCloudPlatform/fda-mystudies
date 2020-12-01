import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SiteCoordinatorRoutingModule} from './site-coordinator-routing.module';
import {SiteCoordinatorComponent} from './sitecoordinator.component';
import {FormsModule} from '@angular/forms';
import {NgxDataTableModule} from 'angular-9-datatable';
import {MobileMenuComponent} from './mobile-menu/mobile-menu.component';
import {DashboardHeaderComponent} from './dashboard-header/dashboard-header.component';
import {ParticipantDetailsComponent} from './participant-details/participant-details.component';
import {SharedModule} from '../shared/shared.module';

@NgModule({
  declarations: [
    SiteCoordinatorComponent,
    MobileMenuComponent,
    DashboardHeaderComponent,
    ParticipantDetailsComponent,
  ],
  imports: [
    CommonModule,
    SiteCoordinatorRoutingModule,
    FormsModule,
    NgxDataTableModule,
    SharedModule,
  ],
})
export class SiteCoordinatorModule {}
