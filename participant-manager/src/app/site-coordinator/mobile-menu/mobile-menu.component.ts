import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {UserService} from 'src/app/service/user.service';
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
  @Output() keyDown: EventEmitter<KeyboardEvent> = new EventEmitter();
  user = {} as Profile;
  showSearchOnClick = false;

  displayHeaderOnResetpassword = true;

  constructor(
    private readonly userService: UserService,
    private readonly displayHeader: HeaderDisplayService,
  ) {}
  ngOnInit(): void {
    this.user = this.userService.getUserProfile();
    this.displayHeader.showHeaders$.subscribe((updatedHeaderDisplayStatus) => {
      this.displayHeaderOnResetpassword = updatedHeaderDisplayStatus;
    });
  }
  toggleNav(): void {
    this.navIsOpen = !this.navIsOpen;
  }

  hamburgerclose(): void {
    // any other execution
    this.navIsOpen = !this.navIsOpen;
  }
  mobileOnKeyDown(event: KeyboardEvent): void {
    this.keyDown.emit(event);
  }
  showSearchBarOnClick(): void {
    this.showSearchOnClick = true;
  }
  hideSearchBarOnClick(): void {
    this.showSearchOnClick = false;
  }
}
