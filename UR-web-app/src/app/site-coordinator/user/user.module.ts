import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {UserRoutingModule} from './user-routing.module';
import {AddNewUserComponent} from './new-user/new-user.component';
import {UserListComponent} from './user-list/user-list.component';
import {UpdateUserComponent} from './update-user/update-user.component';
import {UserDetailsComponent} from './user-details/user-details.component';
import {FormsModule} from '@angular/forms';
import {NgSelectModule} from '@ng-select/ng-select';
import {NgxDataTableModule} from 'angular-9-datatable';

@NgModule({
  declarations: [
    AddNewUserComponent,
    UserListComponent,
    UpdateUserComponent,
    UserDetailsComponent,
  ],
  imports: [
    CommonModule,
    UserRoutingModule,
    FormsModule,
    NgSelectModule,
    NgxDataTableModule,
  ],
})
export class UserModule {}
