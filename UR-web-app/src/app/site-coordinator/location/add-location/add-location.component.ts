import {Component, Input} from '@angular/core';
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
  location = <Location>{};
  sub: Subscription = new Subscription();
  constructor(
    private readonly router: Router,
    private readonly locationService: LocationService,
    private readonly toastr: ToastrService,
  ) {}

  addLocation(): void {
    this.sub = this.locationService.addLocation(this.location).subscribe(() => {
      this.toastr.success('New location added Successfully');
      void this.router.navigate(['/coordinator/locations']);
    });
  }
  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
