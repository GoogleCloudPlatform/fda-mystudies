import {NgModule} from '@angular/core';
import {LocationRoutingModule} from './location-routing.module';
import {AddLocationComponent} from './add-location/add-location.component';
import {LocationDetailsComponent} from './location-details/location-details.component';
import {LocationListComponent} from './location-list/location-list.component';
import {NgxDataTableModule} from 'angular-9-datatable';
import {CommonComponentsModule} from '../../shared/commomComponentsModule';

@NgModule({
  declarations: [
    AddLocationComponent,
    LocationDetailsComponent,
    LocationListComponent,
  ],
  imports: [CommonComponentsModule, LocationRoutingModule, NgxDataTableModule],
})
export class LocationModule {}
