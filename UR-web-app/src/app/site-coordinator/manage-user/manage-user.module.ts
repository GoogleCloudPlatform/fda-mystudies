import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ManageUserService} from './shared/manage-user.service';
import {ManageUserRoutingModule} from './manage-user-routing.module';
import {AddNewUserComponent} from './add-new-user/add-new-user.component';
import {UserListComponent} from
  './user-list/user-list.component';
import {UpdateUserComponent} from './update-user/update-user.component';
import {UserDetailsComponent} from './user-details/user-details.component';


@NgModule({
  declarations:
   [AddNewUserComponent,
     UserListComponent,
     UpdateUserComponent,
     UserDetailsComponent],
  imports: [
    CommonModule,
    ManageUserRoutingModule,
  ],
  providers: [ManageUserService],
})
export class ManageUserModule { }
