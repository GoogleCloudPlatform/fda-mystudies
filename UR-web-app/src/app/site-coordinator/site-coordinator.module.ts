import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SiteCoordinatorRoutingModule} from './site-coordinator-routing.module';
import {SiteCoordinatorComponent} from './sitecoordinator.component';
import {FormsModule} from '@angular/forms';

@NgModule({
  declarations: [SiteCoordinatorComponent],
  imports: [CommonModule, SiteCoordinatorRoutingModule, FormsModule],
})
export class SiteCoordinatorModule {}
