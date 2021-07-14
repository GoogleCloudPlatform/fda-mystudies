import {Component, OnInit} from '@angular/core';
import {SearchService} from '../shared/search.service';
import {SearchBar} from '../shared/search-bar';
import {Profile} from './account/shared/profile.model';
import {UserService} from '../service/user.service';
import {StateService} from '../service/state.service';
import {HeaderDisplayService} from '../service/header-display.service';
import {SearchParameterService} from '../service/search-parameter.service';
import {BnNgIdleService} from 'bn-ng-idle';
import {AccountService} from './account/shared/account.service';
import {Router} from '@angular/router';

@Component({
  selector: 'site-coordinator',
  templateUrl: './sitecoordinator.component.html',
  styleUrls: ['./sitecoordinator.component.scss'],
})
export class SiteCoordinatorComponent implements OnInit {
  searchPlaceholder = 'Search by site or study ID or name';
  showSearchBar = false;
  filterQuery = '';
  searchBar: SearchBar | undefined;
  user = {} as Profile;
  userName = '';
  displayHeaderOnResetpassword = true;
  constructor(
    private readonly searchService: SearchService,
    private readonly userService: UserService,
    private readonly userState: StateService,
    private readonly displayHeader: HeaderDisplayService,
    private readonly searchParameter: SearchParameterService,
    private readonly bnIdle: BnNgIdleService,
    private readonly accountService: AccountService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.showSearchBar = false;
    this.user = this.userService.getUserProfile();
    this.userState.currentUserName$.subscribe((upadtedUsername) => {
      this.userName = upadtedUsername;
    });

    this.displayHeader.showHeaders$.subscribe((updatedHeaderDisplayStatus) => {
      this.displayHeaderOnResetpassword = updatedHeaderDisplayStatus;
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
    this.bnIdle.startWatching(1800).subscribe((isTimedOut: boolean) => {
      if (isTimedOut) {
        this.accountService.logout().subscribe(() => {
          sessionStorage.clear();
          void this.router.navigate(['/']);
        });
      }
    });
  }
  public onKeyUp(event: KeyboardEvent): void {
    if (event.key === 'Enter' && this.searchBar) {
      this.searchParameter.setSearchParameter(this.filterQuery);
      void this.searchBar.search(this.filterQuery);
    } else if (this.searchBar && this.filterQuery === '') {
      this.searchParameter.setSearchParameter('');
      void this.searchBar.search(this.filterQuery);
    }
  }

  onActivate(componentRef: SearchBar): void {
    this.showSearchBar = false;
    this.filterQuery = '';
    this.searchBar = componentRef;
  }

  public mobileFilterQuery(ref: string) {
    this.searchParameter.setSearchParameter(ref);
    void this.searchBar?.search(ref);
  }
}
