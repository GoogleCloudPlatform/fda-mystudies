import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {AddLocationComponent} from './add-location/add-location.component';
import {LocationDetailsComponent} from
  './location-details/location-details.component';
import {LocationListComponent} from
  './location-list/location-list.component';

const routes: Routes = [
  {path: 'location-list', component: LocationListComponent},
  {path: 'add-new-location', component: AddLocationComponent},
  {
    path: 'location-details/:locationId',
    component: LocationDetailsComponent,
  },
  {path: '', redirectTo: 'location-list', pathMatch: 'full'},

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ManageLocationRoutingModule { }
