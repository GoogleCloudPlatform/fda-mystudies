import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AccountRoutingModule} from './account-routing.module';
import {AccountProfileComponent} from './account-profile/account-profile.component';
import {AccountService} from './shared/account.service';
@NgModule({
  declarations: [AccountProfileComponent],
  imports: [CommonModule, AccountRoutingModule],
  providers: [AccountService],
})
export class AccountModule {}
