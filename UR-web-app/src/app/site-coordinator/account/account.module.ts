import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AccountRoutingModule} from './account-routing.module';
import {AccountProfileComponent} from './account-profile/account-profile.component';
@NgModule({
  declarations: [AccountProfileComponent],
  imports: [CommonModule, AccountRoutingModule],
})
export class AccountModule {}
