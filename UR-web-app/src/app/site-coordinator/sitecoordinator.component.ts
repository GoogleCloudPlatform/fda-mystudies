import {Component} from '@angular/core';

@Component({
  selector: 'site-coordinator',
  templateUrl: './sitecoordinator.component.html',
  styleUrls: ['./sitecoordinator.component.scss'],
})
export class SiteCoordinatorComponent {
  searchPlaceholder = 'Search by Site or Study ID or Name';
  opencloseNavval = false;
  showSearchBar = true;
  filterQuery = '';

  opencloseNav(): void {
    this.opencloseNavval = !this.opencloseNavval;
  }

  public handleKeyDown(event: KeyboardEvent): void {
    if (event.keyCode === 13) {
      // :TO DO child component search method call
    }
  }
}
