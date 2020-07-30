import {Component, Input, EventEmitter, Output} from '@angular/core';
import {Location} from '../shared/location.model';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {LocationService} from '../shared/location.service';
import {ToastrService} from 'ngx-toastr';
@Component({
  selector: 'edit-location',
  templateUrl: './edit-location.component.html',
  styleUrls: ['./edit-location.component.scss'],
})
export class EditLocationComponent extends UnsubscribeOnDestroyAdapter {
  @Input() location = {} as Location;
  @Input() locationBackup = {} as Location;
  @Input() locationId = '';
  @Output() closeModalEvent = new EventEmitter<Location>();

  constructor(
    private readonly locationService: LocationService,
    private readonly toastr: ToastrService,
  ) {
    super();
  }
  closeModal(): void {
    this.closeModalEvent.next(this.location);
  }
  update(): void {
    const locationToUpdate = {} as Location;
    locationToUpdate.name = this.location.name;
    locationToUpdate.description = this.location.description;
    this.updateLocation(locationToUpdate);
  }
  changeStatus(): void {
    const locationToUpdate = {} as Location;
    locationToUpdate.status = this.location.status === '1' ? '0' : '1';
    this.updateLocation(locationToUpdate);
  }

  updateLocation(locationToUpdate: Location): void {
    this.subs.add(
      this.locationService.update(locationToUpdate, this.locationId).subscribe(
        (location: Location) => {
          if (getMessage(location.code)) {
            this.toastr.success(getMessage(location.code));
          } else {
            this.toastr.success('Success');
          }
          this.location.name = location.name;
          this.location.description = location.description;
          this.location.status = location.status;
          this.closeModal();
        },
        () => {
          this.closeModal();
        },
      ),
    );
  }
}
