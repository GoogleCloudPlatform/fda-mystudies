import {Component, OnInit} from '@angular/core';
import {ManageUsers} from '../shared/manage-user';
import {combineLatest, BehaviorSubject, Observable, of} from 'rxjs';
import {User} from 'src/app/entity/user';
import {UserService} from '../shared/user.service';
import {map} from 'rxjs/operators';

@Component({
  selector: 'user-list',
  templateUrl: './user-list.component.html',
})
export class UserListComponent implements OnInit {
  manageUser$: Observable<ManageUsers> = of();
  query$ = new BehaviorSubject('');

  constructor(private readonly userService: UserService) {}

  ngOnInit(): void {
    this.getUsers();
  }

  getUsers(): void {
    this.manageUser$ = combineLatest(
      this.userService.getUsers(),
      this.query$,
    ).pipe(
      map(([manageUser, query]) => {
        manageUser.users = manageUser.users.filter(
          (user: User) =>
            user.firstName.toLowerCase().includes(query) ||
            user.lastName.toLowerCase().includes(query) ||
            user.email.toLowerCase().includes(query),
        );
        return manageUser;
      }),
    );
  }

  search(query: string): void {
    this.query$.next(query.trim().toLowerCase());
  }
}
