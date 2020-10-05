import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {AddNewUserComponent} from './new-user/new-user.component';
import {UserListComponent} from './user-list/user-list.component';
import {UpdateUserComponent} from './update-user/update-user.component';
import {UserDetailsComponent} from './user-details/user-details.component';

const routes: Routes = [
  {path: 'new', component: AddNewUserComponent},
  {path: ':userId/edit', component: UpdateUserComponent},
  {path: ':userId', component: UserDetailsComponent},
  {path: '', component: UserListComponent},
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserRoutingModule {}
