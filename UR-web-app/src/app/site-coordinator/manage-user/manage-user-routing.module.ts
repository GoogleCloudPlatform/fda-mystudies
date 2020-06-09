import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {AddNewUserComponent} from './add-new-user/add-new-user.component';
import {UserListComponent} from
  './user-list/user-list.component';
import {UpdateUserComponent} from './update-user/update-user.component';
import {UserDetailsComponent} from './user-details/user-details.component';

const routes: Routes = [
  {path: '', redirectTo: 'user-list', pathMatch: 'full'},
  {path: 'user-list', component: UserListComponent},
  {path: 'add-new-user', component: AddNewUserComponent},
  {path: 'update-user', component: UpdateUserComponent},
  {path: 'user-details', component: UserDetailsComponent},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ManageUserRoutingModule { }
