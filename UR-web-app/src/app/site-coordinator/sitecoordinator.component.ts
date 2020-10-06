import {Component, OnInit} from '@angular/core';
import {SearchService} from '../shared/search.service';
import {SearchBar} from '../shared/search-bar';
import {Profile} from './account/shared/profile.model';
import {UserService} from '../service/user.service';

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

  constructor(
    private readonly searchService: SearchService,
    private readonly userService: UserService,
  ) {}

  ngOnInit(): void {
    this.user = this.userService.getUserProfile();
    this.searchService.searchPlaceHolder$.subscribe(
      (updatedPlaceHolder: string) => {
        this.showSearchBar = true;
        this.searchPlaceholder = updatedPlaceHolder;
      },
    );
  }
  public onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && this.searchBar) {
      void this.searchBar.search(this.filterQuery);
    }
  }
  onActivate(componentRef: SearchBar): void {
    this.showSearchBar = false;
    this.filterQuery = '';
    this.searchBar = componentRef;
  }
}
