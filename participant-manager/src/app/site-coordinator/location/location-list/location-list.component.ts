import {Component, OnInit} from '@angular/core';
import {LocationService} from '../shared/location.service';
import {Location, ManageLocations} from '../shared/location.model';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
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
  query$ = new BehaviorSubject('');
  location$: Observable<ManageLocations> = of();
  manageLocationBackup = {} as ManageLocations;
  permission=Permission;
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
      this.locationService.getLocations(),
      this.query$,
    ).pipe(
      map(([manageLocations, query]) => {
        this.manageLocationBackup = {...manageLocations};
        this.manageLocationBackup.locations = this.manageLocationBackup.locations.filter(
          (location: Location) =>
            (location.name &&
              location.name.toLowerCase().includes(query.toLowerCase())) ||
            (location.customId &&
              location.customId.toLowerCase().includes(query.toLowerCase())),
        );
        return this.manageLocationBackup;
      }),
    );
  }
  search(query: string): void {
    this.query$.next(query.trim());
  }

  locationDetails(locationId: number): void {
    void this.router.navigate(['/coordinator/locations/', locationId]);
  }
  addLocation(): void {
    void this.router.navigate(['/coordinator/locations/new']);
  }
}
