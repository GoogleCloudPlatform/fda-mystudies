import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from '../shared/location.model';
import {LocationService} from '../shared/location.service';
import {ToastrService} from 'ngx-toastr';
import {Subscription} from 'rxjs';
import {getMessage} from 'src/app/shared/success.codes.enum';

@Component({
  selector: 'location-add',
  templateUrl: './add-location.component.html',
  styleUrls: ['./add-location.component.scss'],
})
export class AddLocationComponent {
  @Input() enabled = true;
  location = {} as Location;
  sub: Subscription = new Subscription();
  constructor(
    private readonly router: Router,
    private readonly locationService: LocationService,
    private readonly toastr: ToastrService,
  ) {}

  addLocation(): void {
    this.sub = this.locationService
      .addLocation(this.location)
      .subscribe((successResponse: Location) => {
        if (getMessage(successResponse.code)) {
          this.toastr.success(getMessage(successResponse.code));
        } else this.toastr.success('Success');
        void this.router.navigate(['/coordinator/locations']);
      });
  }
  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
