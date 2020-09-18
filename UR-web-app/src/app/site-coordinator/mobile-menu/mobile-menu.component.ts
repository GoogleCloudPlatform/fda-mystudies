import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {Profile} from '../account/shared/profile.model';

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

  ngOnInit(): void {
    const userObject = sessionStorage.getItem('user');
    if (userObject) this.user = JSON.parse(userObject) as Profile;
  }
  toggleNav(): void {
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
