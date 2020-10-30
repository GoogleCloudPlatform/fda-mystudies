import {Component, OnInit} from '@angular/core';
import {ManageUsers} from '../shared/manage-user';
import {combineLatest, BehaviorSubject, Observable, of} from 'rxjs';
import {User} from 'src/app/entity/user';
import {UserService} from '../shared/user.service';
import {map} from 'rxjs/operators';
import {SearchService} from 'src/app/shared/search.service';
import { Status } from 'src/app/shared/enums';

@Component({
  selector: 'user-list',
  templateUrl: './user-list.component.html',
})
export class UserListComponent implements OnInit {
  manageUser$: Observable<ManageUsers> = of();
  manageUsersBackup = {} as ManageUsers;
onBoardingStatus=Status;
  query$ = new BehaviorSubject('');

  constructor(
    private readonly userService: UserService,
    private readonly sharedService: SearchService,
  ) {}

  ngOnInit(): void {
    this.sharedService.updateSearchPlaceHolder(
      'Search User by Name or Email ID',
    );
    this.getUsers();
  }

  getUsers(): void {
    this.manageUser$ = combineLatest(
      this.userService.getUsers(),
      this.query$,
    ).pipe(
      map(([manageUser, query]) => {
        this.manageUsersBackup = {...manageUser};
        this.manageUsersBackup.users = this.manageUsersBackup.users.filter(
          (user: User) =>
            user.firstName?.toLowerCase().includes(query) ||
            user.lastName?.toLowerCase().includes(query) ||
            user.email?.toLowerCase().includes(query),
        );
        return this.manageUsersBackup;
      }),
    );
  }

  search(query: string): void {
    this.query$.next(query.trim().toLowerCase());
  }
    statusColour(status:string):string {
if (status === this.onBoardingStatus.Active) {
  return 'txt__green'
} else if (status ===this.onBoardingStatus.Deactivated) {
  return 'txt__light-gray'
} else {
return 'txt__space-gray'
}
}
}
