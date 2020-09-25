import {Component, OnInit, TemplateRef} from '@angular/core';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {ActivatedRoute, Router} from '@angular/router';
import {Location} from '../shared/location.model';
import {LocationService} from '../shared/location.service';
import {ToastrService} from 'ngx-toastr';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';

@Component({
  selector: 'location-details',
  templateUrl: './location-details.component.html',
  styleUrls: ['./location-details.component.scss'],
})
export class LocationDetailsComponent
  extends UnsubscribeOnDestroyAdapter
  implements OnInit {
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
  openModal(template: TemplateRef<unknown>): void {
    this.modalRef = this.modalService.show(template);
  }
  closeModal(location: Location): void {
    this.location = location;
    this.locationBackup = JSON.parse(JSON.stringify(this.location)) as Location;
    this.modalRef.hide();
  }
  ngOnInit(): void {
    this.subs.add(
      this.route.params.subscribe((params) => {
        if (params['locationId']) {
          this.locationId = params.locationId as string;
        }
        this.getLocationDetails();
      }),
    );
  }
  getLocationDetails(): void {
    this.locationService.get(this.locationId).subscribe((data: Location) => {
      this.location = data;
      this.locationBackup = JSON.parse(
        JSON.stringify(this.location),
      ) as Location;
    });
  }
}
