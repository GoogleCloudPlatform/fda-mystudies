import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {LocationRoutingModule} from './location-routing.module';
import {AddLocationComponent} from './add-location/add-location.component';
import {LocationDetailsComponent} from './location-details/location-details.component';
import {LocationListComponent} from './location-list/location-list.component';

@NgModule({
  declarations: [
    AddLocationComponent,
    LocationDetailsComponent,
    LocationListComponent,
  ],
  imports: [CommonModule, LocationRoutingModule],
  providers: [],
})
export class LocationModule {}
