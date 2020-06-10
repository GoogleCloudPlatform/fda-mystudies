import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {SiteCoordinatorComponent} from './sitecoordinator.component';


const routes: Routes = [
  {
    path: '',
    component: SiteCoordinatorComponent,
    children: [
      {
        path: 'participants',
        loadChildren:
      () => import('./participant/participant.module').
          then((m) => m.ParticipantModule),
      },
      {
        path: 'accounts',
        loadChildren:
          () => import('./account/account.module').
              then((m) => m.AccountModule),
      },
      {
        path: 'locations',
        loadChildren:
          () => import('./location/location.module').
              then((m) => m.LocationModule),
      },
      {
        path: 'users',
        loadChildren:
          () => import('./user/user.module').
              then((m) => m.UserModule),
      },
      {path: '', redirectTo: 'participants', pathMatch: 'full'},
    ],

  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SiteCoordinatorRoutingModule { }
