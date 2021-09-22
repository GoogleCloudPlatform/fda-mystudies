import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {UserService} from 'src/app/service/user.service';
import {SearchService} from 'src/app/shared/search.service';
import {Profile} from '../account/shared/profile.model';

import {HeaderDisplayService} from '../../service/header-display.service';

@Component({
  selector: 'mobile-menu',
  templateUrl: './mobile-menu.component.html',
  styleUrls: ['./mobile-menu.component.scss'],
})
export class MobileMenuComponent implements OnInit {
  @Input() searchPlaceholder = 'Search by Site or Study ID or Name';
  navIsOpen = false;
  @Input() showSearchBar = true;
  @Input() filterQuery = '';
  mobileFilterQuery = '';
  @Output() keyDown: EventEmitter<string> = new EventEmitter();
  user = {} as Profile;
  showSearchOnClick = false;
  updatedPlaceHolder = '';
  displayHeaderOnResetpassword = true;

  constructor(
    private readonly userService: UserService,
    private readonly searchService: SearchService,
    private readonly displayHeader: HeaderDisplayService,
  ) {}
  ngOnInit(): void {
    this.user = this.userService.getUserProfile();
    this.displayHeader.showHeaders$.subscribe((updatedHeaderDisplayStatus) => {
      this.displayHeaderOnResetpassword = updatedHeaderDisplayStatus;
    });
    this.searchService.searchPlaceHolder$.subscribe(
      (updatedPlaceHolder: string) => {
        this.showSearchBar = true;
        this.mobileFilterQuery = '';
        this.updatedPlaceHolder = updatedPlaceHolder;
      },
    );
  }

  toggleNav(): void {
    this.navIsOpen = !this.navIsOpen;
  }

  hamburgerclose(): void {
    // any other execution
    this.navIsOpen = !this.navIsOpen;
  }
  mobileOnKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.keyDown.emit(this.mobileFilterQuery);
      this.showSearchOnClick = false;
    }
  }
  showSearchBarOnClick(): void {
    this.showSearchOnClick = true;
  }
  hideSearchBarOnClick(): void {
    this.showSearchOnClick = false;
  }
}
