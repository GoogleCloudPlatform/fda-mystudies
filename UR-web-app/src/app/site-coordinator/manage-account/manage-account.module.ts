import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ManageAccountService} from './shared/manage-account.service';
import {ManageAccountRoutingModule} from './manage-account-routing.module';
import {UserProfileComponent} from './user-profile/user-profile.component';
@NgModule({
  declarations: [UserProfileComponent],
  imports: [
    CommonModule,
    ManageAccountRoutingModule,
  ],
  providers: [ManageAccountService],
})
export class ManageAccountModule { }
