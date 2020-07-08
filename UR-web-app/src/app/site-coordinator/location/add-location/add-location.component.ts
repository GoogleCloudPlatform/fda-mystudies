import {Component, Input, EventEmitter, Output} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from '../shared/location.model';
import {LocationService} from '../shared/location.service';
import {ToastrService} from 'ngx-toastr';
import {Subscription} from 'rxjs';

@Component({
  selector: 'location-add',
  templateUrl: './add-location.component.html',
  styleUrls: ['./add-location.component.scss'],
})
export class AddLocationComponent {
  @Input() enabled = true;
  @Output() onFormSubmit: EventEmitter<unknown> = new EventEmitter<unknown>();
  location = <Location>{};
  sub: Subscription = new Subscription();
  constructor(
    private readonly router: Router,
    private readonly locationService: LocationService,
    private readonly toastr: ToastrService,
  ) {}

  addLocation(): void {
    this.locationService.addLocation(this.location).subscribe(() => {
      this.toastr.success('New location added successfully');
      void this.router.navigate(['/coordinator/locations']);
    });
  }
  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
