import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SiteCoordinatorRoutingModule} from './site-coordinator-routing.module';
import {SiteCoordinatorComponent} from './sitecoordinator.component';
import {FormsModule} from '@angular/forms';
import { MobileMenuComponent } from './mobile-menu/mobile-menu.component';

@NgModule({
  declarations: [SiteCoordinatorComponent, MobileMenuComponent],
  imports: [CommonModule, SiteCoordinatorRoutingModule, FormsModule],
})
export class SiteCoordinatorModule {}
