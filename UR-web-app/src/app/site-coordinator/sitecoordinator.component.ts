import {Component, OnInit} from '@angular/core';
import {SharedService} from '../shared/shared.service';
import {SearchBarGeneric} from '../shared/search-bar-generic';

@Component({
  selector: 'site-coordinator',
  templateUrl: './sitecoordinator.component.html',
  styleUrls: ['./sitecoordinator.component.scss'],
})
export class SiteCoordinatorComponent implements OnInit {
  searchPlaceholder = 'Search by Site or Study ID or Name';
  showSearchBar = false;
  filterQuery = '';
  componentRef!: SearchBarGeneric;

  constructor(private readonly sharedService: SharedService) {}

  ngOnInit(): void {
    this.sharedService.updatedSearchPlaceHolder.subscribe(
      (updatedPlaceHolder: string) => {
        this.showSearchBar = true;
        this.searchPlaceholder = updatedPlaceHolder;
      },
    );
  }
  public onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      void this.componentRef.search(this.filterQuery);
    }
  }
  onActivate(componentRef: SearchBarGeneric): void {
    this.showSearchBar = false;
    this.filterQuery = '';
    this.componentRef = componentRef;
  }
}
