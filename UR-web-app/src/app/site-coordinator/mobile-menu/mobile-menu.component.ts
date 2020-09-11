import {Component, Input, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'mobile-menu',
  templateUrl: './mobile-menu.component.html',
  styleUrls: ['./mobile-menu.component.scss'],
})
export class MobileMenuComponent {
  @Input() searchPlaceholder = 'Search by Site or Study ID or Name';
  navIsOpen = false;
  @Input() showSearchBar = true;
  @Input() filterQuery = '';
  @Output() keyDown: EventEmitter<KeyboardEvent> = new EventEmitter();
  showSearchOnClick = false;

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
