import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {AccountProfileComponent} from './account-profile/account-profile.component';
import {ChangePasswordComponent} from './change-password/change-password.component';
const routes: Routes = [
  {path: '', component: AccountProfileComponent},
  {path: 'changepassword', component: ChangePasswordComponent},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountRoutingModule {}
