import {Component, OnInit} from '@angular/core';
import {LocationService} from '../shared/location.service';
import {ManageLocations} from '../shared/location.model';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {of} from 'rxjs';
import {SearchService} from 'src/app/shared/search.service';
import {Permission} from 'src/app/shared/permission-enums';

@Component({
  selector: 'location-list',
  templateUrl: './location-list.component.html',
  styleUrls: ['./location-list.component.scss'],
})
export class LocationListComponent implements OnInit {
  location$: Observable<ManageLocations> = of();
  permission = Permission;
  // pagination
  limit = 10;
  currentPage = 1;
  offset = 0;
  searchTerm = '';
  sortBy: string[] | string = ['_locationId'];
  sortOrder = 'asc';
  constructor(
    private readonly locationService: LocationService,
    private readonly router: Router,
    private readonly toastr: ToastrService,
    private readonly sharedService: SearchService,
  ) {}

  ngOnInit(): void {
    this.sharedService.updateSearchPlaceHolder('Search Location');
    this.getLocation();
  }

  getLocation(): void {
    this.location$ = combineLatest(
      this.locationService.getLocations(
        this.offset,
        this.limit,
        this.searchTerm,
        this.sortBy[0].replace('_', ''),
        this.sortOrder,
      ),
    ).pipe(
      map(([manageLocations]) => {
        return manageLocations;
      }),
    );
  }
  search(query: string): void {
    this.currentPage = 1;
    this.offset = 0;
    this.searchTerm = query.trim().toLowerCase();
    this.getLocation();
  }

  pageChange(page: number): void {
    if (page >= 1) {
      this.currentPage = page;
      this.offset = (page - 1) * this.limit;
      this.getLocation();
    }
  }

  public onSortOrder(event: string): void {
    this.sortOrder = event;
    this.offset = 0;
    this.currentPage = 0;
    this.getLocation();
  }

  public onSortBy(event: string | string[]): void {
    this.sortBy = new Array(event) as string[];
    this.offset = 0;
    this.currentPage = 0;
    if (this.sortBy[0] === '_status') {
      this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
    }
    this.getLocation();
  }

  locationDetails(locationId: number): void {
    void this.router.navigate(['/coordinator/locations/', locationId]);
  }
  addLocation(): void {
    void this.router.navigate(['/coordinator/locations/new']);
  }
}
