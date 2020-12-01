import {Component, OnInit} from '@angular/core';
import {SearchService} from '../shared/search.service';
import {SearchBar} from '../shared/search-bar';
import {Profile} from './account/shared/profile.model';
import {UserService} from '../service/user.service';
import {StateService} from '../service/state.service';

@Component({
  selector: 'site-coordinator',
  templateUrl: './sitecoordinator.component.html',
  styleUrls: ['./sitecoordinator.component.scss'],
})
export class SiteCoordinatorComponent implements OnInit {
  searchPlaceholder = 'Search by Site or Study ID or Name';
  showSearchBar = false;
  filterQuery = '';
  searchBar: SearchBar | undefined;
  user = {} as Profile;
  userName = '';

  constructor(
    private readonly searchService: SearchService,
    private readonly userService: UserService,
    private readonly userState: StateService,
  ) {}

  ngOnInit(): void {
    this.showSearchBar = false;
    this.user = this.userService.getUserProfile();
    this.userState.currentUserName$.subscribe((upadtedUsername) => {
      this.userName = upadtedUsername;
    });
    if (this.userName === '') {
      this.userName = this.user.firstName;
    }
    this.searchService.searchPlaceHolder$.subscribe(
      (updatedPlaceHolder: string) => {
        if (updatedPlaceHolder) {
          this.showSearchBar = true;
          this.filterQuery = '';
          this.searchPlaceholder = updatedPlaceHolder;
        }
      },
    );
  }
  public onKeyUp(event: KeyboardEvent): void {
    if (event.key === 'Enter' && this.searchBar) {
      void this.searchBar.search(this.filterQuery);
    } else if (this.searchBar && this.filterQuery === '') {
      void this.searchBar.search(this.filterQuery);
    }
  }
  onActivate(componentRef: SearchBar): void {
    this.showSearchBar = false;
    this.filterQuery = '';
    this.searchBar = componentRef;
  }
}
