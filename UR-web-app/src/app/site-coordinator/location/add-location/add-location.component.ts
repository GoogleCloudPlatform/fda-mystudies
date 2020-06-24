import {Component, ViewChild, TemplateRef} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from '../shared/location.model';
import {LocationService} from '../shared/location.service';
import {ToastrService} from 'ngx-toastr';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {NgForm, NgModel} from '@angular/forms';
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
      (data: ApiSuccessResponse) => {
        this.toastr.success(data.successBean.message);
      },
      (error: ApiResponse) => {
        this.toastr.error(error.error.userMessage);
      },
    );
  }
}
