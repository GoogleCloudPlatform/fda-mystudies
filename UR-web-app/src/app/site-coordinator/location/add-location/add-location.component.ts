import {Component, Input, Output, EventEmitter} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from '../shared/location.model';
import {LocationService} from '../shared/location.service';
import {ToastrService} from 'ngx-toastr';
import {ApiResponse} from 'src/app/entity/error.model';
import {ApiSuccessResponse} from 'src/app/entity/sucess.model';

@Component({
  selector: 'location-add',
  templateUrl: './add-location.component.html',
  styleUrls: ['./add-location.component.scss'],
})
export class AddLocationComponent {
  @Input() enabled = true;
  @Output() onFormSubmit: EventEmitter<unknown> = new EventEmitter<unknown>();
  location: Location = new Location();
  constructor(
    private readonly router: Router,
    private readonly locationService: LocationService,
    private readonly toastr: ToastrService,
  ) {}

  addLocation(): void {
    this.locationService.addLocation(this.location).subscribe(
      (succesResponse: unknown) => {
        const successData = succesResponse as ApiSuccessResponse;
        this.toastr.success(successData.successBean.message);
        void this.router.navigate(['/coordinator/locations']);
      },
      (errorResponse: unknown) => {
        const errorData = errorResponse as ApiResponse;
        this.toastr.error(errorData.error.userMessage);
      },
    );
  }
}
