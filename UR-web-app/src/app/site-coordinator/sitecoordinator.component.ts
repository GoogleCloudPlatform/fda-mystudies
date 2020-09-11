import {Component, OnInit} from '@angular/core';
import {SearchService} from '../shared/search.service';
import {SearchBar} from '../shared/search-bar';

@Component({
  selector: 'site-coordinator',
  templateUrl: './sitecoordinator.component.html',
  styleUrls: ['./sitecoordinator.component.scss'],
})
export class SiteCoordinatorComponent implements OnInit {
  searchPlaceholder = 'Search by Site or Study ID or Name';
  showSearchBar = false;
  filterQuery = '';
  searchBar: SearchBar | undefined;

  constructor(private readonly searchService: SearchService) {}

  ngOnInit(): void {
    this.searchService.searchPlaceHolder$.subscribe(
      (updatedPlaceHolder: string) => {
        this.showSearchBar = true;
        this.searchPlaceholder = updatedPlaceHolder;
      },
    );
  }
  public onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && this.searchBar) {
      void this.searchBar.search(this.filterQuery);
    }
  }
  onActivate(componentRef: SearchBar): void {
    this.showSearchBar = false;
    this.filterQuery = '';
    this.searchBar = componentRef;
  }
}
