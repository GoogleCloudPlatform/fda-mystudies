import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {UserProfileComponent} from './user-profile/user-profile.component';

const routes: Routes = [
  {path: '', component: UserProfileComponent},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountRoutingModule { }
