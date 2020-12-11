import {Component, OnInit} from '@angular/core';
import {ManageUsers} from '../shared/manage-user';
import {combineLatest, Observable, of} from 'rxjs';
import {UserService} from '../shared/user.service';
import {map} from 'rxjs/operators';
import {SearchService} from 'src/app/shared/search.service';
import {Status} from 'src/app/shared/enums';

@Component({
  selector: 'user-list',
  templateUrl: './user-list.component.html',
})
export class UserListComponent implements OnInit {
  manageUser$: Observable<ManageUsers> = of();
  onBoardingStatus = Status;
  // pagination
  limit = 10;
  currentPage = 1;
  offset = 0;
  searchTerm = '';
  sortBy: string[] | string = ['_firstName'];
  sortOrder = 'asc';
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
      this.userService.getUsers(
        this.offset,
        this.limit,
        this.searchTerm,
        this.sortBy[0].replace('_', ''),
        this.sortOrder,
      ),
    ).pipe(
      map(([manageUser]) => {
        return manageUser;
      }),
    );
  }

  search(query: string): void {
    this.currentPage = 1;
    this.offset = 0;
    this.searchTerm = query.trim().toLowerCase();
    this.getUsers();
  }

  pageChange(page: number): void {
    if (page >= 1) {
      this.currentPage = page;
      this.offset = (page - 1) * this.limit;
      this.getUsers();
    }
  }

  public onSortOrder(event: string): void {
    this.sortOrder = event;
    this.offset = 0;
    this.currentPage = 0;
    this.getUsers();
  }

  public onSortBy(event: string | string[]): void {
    this.sortBy = new Array(event) as string[];
    this.offset = 0;
    this.currentPage = 0;
    this.getUsers();
  }

  statusColour(status: string): string {
    if (status === this.onBoardingStatus.Active) {
      return 'txt__green';
    } else if (status === this.onBoardingStatus.Deactivated) {
      return 'txt__light-gray';
    } else {
      return 'txt__space-gray';
    }
  }
}
