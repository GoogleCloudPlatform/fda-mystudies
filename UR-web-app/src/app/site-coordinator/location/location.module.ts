import {NgModule} from '@angular/core';
import {LocationRoutingModule} from './location-routing.module';
import {AddLocationComponent} from './add-location/add-location.component';
import {LocationDetailsComponent} from './location-details/location-details.component';
import {LocationListComponent} from './location-list/location-list.component';
import {NgxDataTableModule} from 'angular-9-datatable';
import {CommonModule} from '@angular/common';
@NgModule({
  declarations: [
    AddLocationComponent,
    LocationDetailsComponent,
    LocationListComponent,
  ],
  imports: [CommonModule, LocationRoutingModule, NgxDataTableModule],
})
export class LocationModule {}
