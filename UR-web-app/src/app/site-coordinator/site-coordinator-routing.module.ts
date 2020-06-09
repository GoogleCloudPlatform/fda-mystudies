import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {SiteCoordinatorComponent} from './sitecoordinator.component';


const routes: Routes = [
  {
    path: '',
    component: SiteCoordinatorComponent,
    children: [
      {
        path: 'manage-sites',
        loadChildren:
      () => import('./manage-sites/manage-sites.module').
          then((m) => m.ManageSitesModule),
      },
      {
        path: 'manage-account',
        loadChildren:
          () => import('./manage-account/manage-account.module').
              then((m) => m.ManageAccountModule),
      },
      {
        path: 'manage-locations',
        loadChildren:
          () => import('./manage-location/manage-location.module').
              then((m) => m.ManageLocationModule),
      },
      {
        path: 'manage-users',
        loadChildren:
          () => import('./manage-user/manage-user.module').
              then((m) => m.ManageUserModule),
      },
      {path: '', redirectTo: 'manage-sites', pathMatch: 'full'},
    ],

  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SiteCoordinatorRoutingModule { }
