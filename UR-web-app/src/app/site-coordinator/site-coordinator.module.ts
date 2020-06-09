import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SiteCoordinatorRoutingModule} from './site-coordinator-routing.module';
import {SiteCoordinatorComponent} from './sitecoordinator.component';

@NgModule({
  declarations: [SiteCoordinatorComponent],
  imports: [
    CommonModule,
    SiteCoordinatorRoutingModule,
  ],
})
export class SiteCoordinatorModule { }
