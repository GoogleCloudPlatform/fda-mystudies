import {Component, OnInit, Input} from '@angular/core';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '../shared/location.model';
import {LocationService} from '../shared/location.service';
import {ToastrService} from 'ngx-toastr';
import {SuccessCodesEnum, getMessage} from 'src/app/shared/success.codes.enum';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';

@Component({
  selector: 'location-details',
  templateUrl: './location-details.component.html',
  styleUrls: ['./location-details.component.scss'],
})
export class LocationDetailsComponent extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  @Input() enabled = true;
  location = {} as Location;
  locationBackup = {} as Location;
  locationId = '';
  constructor(
    private readonly modalService: BsModalService,
    private modalRef: BsModalRef,
    private readonly locationService: LocationService,
    private readonly toastr: ToastrService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {
    super();
  }
  openModal(template: unknown): void {
    this.modalRef = this.modalService.show(template);
  }
  closeModal(): void {
    this.modalRef.hide();
  }
  ngOnInit(): void {
    this.subs.add(
      this.route.params.subscribe((params) => {
        if (params['locationId']) {
          this.locationId = params.locationId as string;
        } else {
          void this.router.navigate(['/coordinator/locations']);
        }
        this.getLocationDetails();
      }),
    );
  }
  getLocationDetails(): void {
    this.subs.add(
      this.locationService
        .getLocationDetails(this.locationId)
        .subscribe((data: Location[]) => {
          this.location = data[0];
          this.locationBackup = JSON.parse(
            JSON.stringify(this.location),
          ) as Location;
        }),
    );
  }
  updateLocation(task: string): void {
    const locationToUpdate = {} as Location;
    if (task === 'update') {
      locationToUpdate.name = this.location.name;
      locationToUpdate.description = this.location.description;
    } else {
      locationToUpdate.status = this.location.status === '1' ? '0' : '1';
    }
    this.subs.add(
      this.locationService
        .updateLocation(locationToUpdate, this.locationId)
        .subscribe((location: Location) => {
          if (getMessage(location.code as keyof typeof SuccessCodesEnum)) {
            this.toastr.success(
              getMessage(location.code as keyof typeof SuccessCodesEnum),
            );
          } else {
            this.toastr.success('Success');
          }

          this.location.name = location.name;
          this.location.description = location.description;
          this.location.status = location.status;
          this.locationBackup = JSON.parse(
            JSON.stringify(this.location),
          ) as Location;
        }),
    );
  }
}
