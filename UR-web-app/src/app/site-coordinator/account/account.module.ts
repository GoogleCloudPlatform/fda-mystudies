import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AccountRoutingModule} from './account-routing.module';
import {AccountProfileComponent} from './account-profile/account-profile.component';
import {ChangePasswordComponent} from './change-password/change-password.component';
import {ReactiveFormsModule} from '@angular/forms';
@NgModule({
  declarations: [AccountProfileComponent, ChangePasswordComponent],
  imports: [CommonModule, AccountRoutingModule, ReactiveFormsModule],
})
export class AccountModule {}
