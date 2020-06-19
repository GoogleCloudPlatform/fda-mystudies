import {Component} from '@angular/core';

@Component({
  selector: 'site-coordinator',
  templateUrl: './sitecoordinator.component.html',
  styleUrls: ['./sitecoordinator.component.scss'],
})
export class SiteCoordinatorComponent {
  searchPlaceholder = 'Search by Site or Study ID or Name';
  navIsOpen = false;
  showSearchBar = true;
  filterQuery = '';

  toggleNav(): void {
    this.navIsOpen = !this.navIsOpen;
  }

  public handleKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      // :TO DO child component search method call
    }
  }
}
