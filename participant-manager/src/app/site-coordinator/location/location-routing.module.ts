import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {AddLocationComponent} from './add-location/add-location.component';
import {LocationDetailsComponent} from './location-details/location-details.component';
import {LocationListComponent} from './location-list/location-list.component';

const routes: Routes = [
  {path: 'new', component: AddLocationComponent},
  {
    path: ':locationId',
    component: LocationDetailsComponent,
  },
  {path: '', component: LocationListComponent},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LocationRoutingModule {}
