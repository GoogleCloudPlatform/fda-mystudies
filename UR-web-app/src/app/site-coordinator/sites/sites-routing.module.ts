import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {SiteListComponent} from './site-list/site-list.component';
import {SiteDetailsComponent} from './site-details/site-details.component';
const routes: Routes = [
  {
    path: ':siteId',
    component: SiteDetailsComponent,
  },
  {
    path: '',
    component: SiteListComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SitesRoutingModule {}
