import {Component} from '@angular/core';

@Component({
  selector: 'site-coordinator',
  templateUrl: './sitecoordinator.component.html',
  styleUrls: ['./sitecoordinator.component.scss'],
})
export class SiteCoordinatorComponent {
  searchPlaceholder = 'Search by Site or Study ID or Name';
  showSearchBar = true;
  filterQuery = '';

  public onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      // :TO DO child component search method call
    }
  }
}
