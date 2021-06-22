import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {UserService} from 'src/app/service/user.service';
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
  mobileFilterQuery='';
  @Output() keyDown: EventEmitter<string> = new EventEmitter();
  user = {} as Profile;
  showSearchOnClick = false;
  constructor(private readonly userService: UserService) {}
  ngOnInit(): void {
    this.user = this.userService.getUserProfile();
  }
  toggleNav(): void {
    this.navIsOpen = !this.navIsOpen;
  }

  hamburgerclose(): void {
// any other execution
  this.navIsOpen = !this.navIsOpen;
}
  mobileOnKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' ){
    this.keyDown.emit(this.mobileFilterQuery);
    }
  }
  showSearchBarOnClick(): void {
    this.showSearchOnClick = true;
  }
  hideSearchBarOnClick(): void {
    this.showSearchOnClick = false;
  }
}
