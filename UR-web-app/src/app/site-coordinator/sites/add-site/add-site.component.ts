import {Component, Input, OnInit, EventEmitter, Output} from '@angular/core';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {SitesService} from '../shared/sites.service';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {ToastrService} from 'ngx-toastr';
import {Study} from '../../studies/shared/study.model';
import {AddSiteRequest} from '../shared/add.sites.request';
import {LocationService} from '../../location/shared/location.service';
import {Location} from '../../location/shared/location.model';
import {ApiResponse} from 'src/app/entity/api.response.model';
@Component({
  selector: 'add-site',
  templateUrl: './add-site.component.html',
  styleUrls: ['./add-site.component.scss'],
})
export class AddSiteComponent extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  @Input() study = {} as Study;
  @Output() closeModalEvent = new EventEmitter();
  newSite = {} as Study;
  site = {} as AddSiteRequest;
  locations: Location[] = [];
  constructor(
    private readonly siteService: SitesService,
    private readonly toastr: ToastrService,
    private readonly locationService: LocationService,
  ) {
    super();
  }
  ngOnInit(): void {
    this.newSite.customId = this.study.customId;
    this.newSite.appId = this.study.appId;
    this.site.studyId = String(this.study.id);
    this.getLocation(this.study.id);
  }
  getLocation(studyId: number): void {
    if (studyId) {
      this.locationService
        .getLocationsForSiteCreation(studyId.toString())
        .subscribe((data) => {
          this.locations = data;
        });
    }
  }
  add(): void {
    this.subs.add(
      this.siteService.add(this.site).subscribe(
        (successResponse: ApiResponse) => {
          if (getMessage(successResponse.code)) {
            this.toastr.success(getMessage(successResponse.code));
          } else {
            this.toastr.success('Success');
          }
          this.closeModal();
        },
        () => {
          this.closeModal();
        },
      ),
    );
  }
  closeModal(): void {
    this.closeModalEvent.next();
  }
}
