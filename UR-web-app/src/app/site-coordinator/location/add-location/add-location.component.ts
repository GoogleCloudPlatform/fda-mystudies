import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from '../shared/location.model';
import {LocationService} from '../shared/location.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'location-add',
  templateUrl: './add-location.component.html',
  styleUrls: ['./add-location.component.scss'],
})
export class AddLocationComponent {
  @Input() enabled = true;
  location = <Location>{};
  constructor(
    private readonly router: Router,
    private readonly locationService: LocationService,
    private readonly toastr: ToastrService,
  ) {}

  addLocation(): void {
    this.locationService.addLocation(this.location).subscribe(
      (succesResponse: Location) => {
        const success = succesResponse.successBean;
        this.toastr.success(success.message);
        void this.router.navigate(['/coordinator/locations']);
      },
      (errorResponse: Location) => {
        const errs = errorResponse.error;
        this.toastr.error(errs.userMessage);
      },
    );
  }
}
