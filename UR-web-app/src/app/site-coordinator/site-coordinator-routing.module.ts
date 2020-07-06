import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {SiteCoordinatorComponent} from './sitecoordinator.component';
import {ParticipantModule} from './participant/participant.module';
import {AccountModule} from './account/account.module';
import {LocationModule} from './location/location.module';
import {UserModule} from './user/user.module';

const routes: Routes = [
  {
    path: '',
    component: SiteCoordinatorComponent,
    children: [
      {
        path: 'participants',
        loadChildren: async (): Promise<ParticipantModule> =>
          import('./participant/participant.module').then(
            (m) => m.ParticipantModule,
          ),
      },
      {
        path: 'accounts',
        loadChildren: async (): Promise<AccountModule> =>
          import('./account/account.module').then((m) => m.AccountModule),
      },
      {
        path: 'locations',
        loadChildren: async (): Promise<LocationModule> =>
          import('./location/location.module').then((m) => m.LocationModule),
      },
      {
        path: 'users',
        loadChildren: async (): Promise<UserModule> =>
          import('./user/user.module').then((m) => m.UserModule),
      },
      {path: '', redirectTo: 'participants', pathMatch: 'full'},
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SiteCoordinatorRoutingModule {}
