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
  @Output('handleKeyDown') handleKeyDown: EventEmitter<
    KeyboardEvent
  > = new EventEmitter();
  showSearchOnClick = false;

  toggleNav(): void {
    this.navIsOpen = !this.navIsOpen;
  }

  mobileHandleKeyDown(event: KeyboardEvent): void {
    this.handleKeyDown.emit(event);
  }
  showSearchBarOnClick() {
    this.showSearchOnClick = true;
  }
  hideSearchBarOnClick() {
    this.showSearchOnClick = false;
  }
}
