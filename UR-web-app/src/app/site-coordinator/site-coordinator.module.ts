import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SiteCoordinatorRoutingModule} from './site-coordinator-routing.module';
import {SiteCoordinatorComponent} from './sitecoordinator.component';
import {FormsModule} from '@angular/forms';
import {MobileMenuComponent} from './mobile-menu/mobile-menu.component';
import {DashboardHeaderComponent} from './dashboard-header/dashboard-header.component';

@NgModule({
  declarations: [
    SiteCoordinatorComponent,
    MobileMenuComponent,
    DashboardHeaderComponent,
  ],
  imports: [CommonModule, SiteCoordinatorRoutingModule, FormsModule],
})
export class SiteCoordinatorModule {}
