import {Component, OnInit} from '@angular/core';
import {LocationService} from '../shared/location.service';
import {Location} from '../shared/location.model';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {of} from 'rxjs';

@Component({
  selector: 'location-list',
  templateUrl: './location-list.component.html',
  styleUrls: ['./location-list.component.scss'],
})
export class LocationListComponent implements OnInit {
  query$ = new BehaviorSubject('');
  location$: Observable<Location[]> = of([]);
  locations: Location[] = [];
  constructor(
    private readonly locationService: LocationService,
    private readonly router: Router,
    private readonly toastr: ToastrService,
  ) {}

  ngOnInit(): void {
    this.getLocation();
  }

  getLocation(): void {
    this.location$ = combineLatest(
      this.locationService.getLocations(),
      this.query$,
    ).pipe(
      map(([locations, query]) => {
        this.locations = locations;
        return locations.filter(
          (location: Location) =>
            (location.name &&
              location.name.toLowerCase().includes(query.toLowerCase())) ||
            (location.customId &&
              location.customId.toLowerCase().includes(query.toLowerCase())),
        );
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
