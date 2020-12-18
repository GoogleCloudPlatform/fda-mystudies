import {Component, Input, EventEmitter, Output} from '@angular/core';
import {
  Location,
  FieldUpdateRequest,
  StatusUpdateRequest,
  UpdateLocationResponse,
} from '../shared/location.model';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {LocationService} from '../shared/location.service';
import {ToastrService} from 'ngx-toastr';
@Component({
  selector: 'edit-location',
  templateUrl: './edit-location.component.html',
  styleUrls: ['./edit-location.component.scss'],
})
export class EditLocationComponent {
  @Input() location = {} as Location;
  @Input() locationId = '';
  @Output() closeModalEvent = new EventEmitter<Location>();
  statusUpdate: StatusUpdateRequest = {status: 0};
  fieldUpdate: FieldUpdateRequest = {
    name: '',
    description: '',
  };
  constructor(
    private readonly locationService: LocationService,
    private readonly toastr: ToastrService,
  ) {}
  ngOnChanges(): void {
    this.statusUpdate.status = this.location.status;
    this.fieldUpdate = {
      description: this.location.description,
      name: this.location.name,
    };
  }
  closeModal(): void {
    this.closeModalEvent.next(this.location);
  }
  update(): void {
    this.updateLocation(this.fieldUpdate);
  }
  toggleStatus(): void {
    this.statusUpdate.status = this.statusUpdate.status === 1 ? 0 : 1;
    this.updateLocation(this.statusUpdate);
  }

  updateLocation(
    locationToUpdate: FieldUpdateRequest | StatusUpdateRequest,
  ): void {
    this.locationService.update(locationToUpdate, this.locationId).subscribe(
      (updatedlocation: UpdateLocationResponse) => {
        if (getMessage(updatedlocation.code)) {
          this.toastr.success(getMessage(updatedlocation.code));
        } else {
          this.toastr.success('Success');
        }
        this.location.name = updatedlocation.name;
        this.location.description = updatedlocation.description;
        this.location.status = updatedlocation.status;
        this.closeModal();
      },
      () => {
        this.closeModal();
      },
    );
  }
}
