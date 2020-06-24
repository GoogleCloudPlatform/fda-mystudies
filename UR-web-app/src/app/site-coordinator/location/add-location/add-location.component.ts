import {Component, TemplateRef} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from '../shared/location.model';
import {LocationService} from '../shared/location.service';
import {ToastrService} from 'ngx-toastr';
import {BsModalService} from 'ngx-bootstrap/modal';
import {ApiResponse} from 'src/app/entity/error.model';
import {ApiSuccessResponse} from 'src/app/entity/sucess.model';

@Component({
  selector: 'location-add',
  templateUrl: './add-location.component.html',
  styleUrls: ['./add-location.component.scss'],
})
export class AddLocationComponent<T> {
  location: Location = new Location();
  constructor(
    private readonly router: Router,
    private readonly modalService: BsModalService,
    private readonly locationService: LocationService,
    private readonly toastr: ToastrService,
  ) {}
  openModal(template: TemplateRef<T>): void {
    this.modalService.show(template);
  }

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
