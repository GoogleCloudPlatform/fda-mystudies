import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ManageLocationService} from './shared/manage-location.service';
import {ManageLocationRoutingModule} from './manage-location-routing.module';
import {AddLocationComponent} from './add-location/add-location.component';
import {LocationDetailsComponent} from
  './location-details/location-details.component';
import {LocationListComponent} from './location-list/location-list.component';


@NgModule({
  declarations: [AddLocationComponent, LocationDetailsComponent,
    LocationListComponent],
  imports: [
    CommonModule,
    ManageLocationRoutingModule,
  ],
  providers: [ManageLocationService],
})
export class ManageLocationModule { }
