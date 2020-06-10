import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AccountRoutingModule} from './account-routing.module';
import {UserProfileComponent} from './user-profile/user-profile.component';
@NgModule({
  declarations: [UserProfileComponent],
  imports: [
    CommonModule,
    AccountRoutingModule,
  ],
  providers: [],
})
export class AccountModule { }
