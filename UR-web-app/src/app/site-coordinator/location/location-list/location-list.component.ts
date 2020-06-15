import {Component, OnInit} from '@angular/core';
import {LocationService} from '../shared/location.service';
import {Location} from '../shared/location.model';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {ErrorBean} from 'src/app/entity/error.model';
@Component({
  selector: 'location-list',
  templateUrl: './location-list.component.html',
  styleUrls: ['./location-list.component.scss'],
})
export class LocationListComponent implements OnInit {
  locations: Location[] = [];
  locationBackup: Location[] = [];
  errorMessage = '';

  constructor(
    private readonly locationService: LocationService,
    private readonly router: Router,
    private readonly toastr: ToastrService,
  ) {}

  ngOnInit(): void {
    this.getLocation();
  }
  getLocation(): void {
    this.locations = [];
    this.locationBackup = [];
    this.locationService.getLocations().subscribe(
      (data) => {
        this.locations = data;
        this.locationBackup = JSON.parse(
          JSON.stringify(this.locations),
        ) as Location[];
      },
      (error: ErrorBean) => {
        this.locations = [];
        this.locationBackup = [];
        this.errorMessage = error.userMessage;
        this.toastr.error(this.errorMessage);
      },
    );
  }
  locationDetails(locationId: number): void {
    void this.router.navigate(['/coordinator/locations/', locationId]);
  }
  addLocation(): void {
    void this.router.navigate(['/coordinator/locations/new']);
  }
  search(filterQuery: string): void {
    const query = filterQuery;
    if (query && query.trim() !== '') {
      this.locations = this.locationBackup.filter(function (a) {
        return (
          (a.name && a.name.toLowerCase().includes(query.toLowerCase())) ||
          (a.customId && a.customId.toLowerCase().includes(query.toLowerCase()))
        );
      });
    } else {
      this.locations = this.locationBackup;
    }
  }
}
