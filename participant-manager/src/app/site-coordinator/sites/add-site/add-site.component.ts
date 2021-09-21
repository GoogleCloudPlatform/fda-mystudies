import {Component, Input, OnInit, EventEmitter, Output} from '@angular/core';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {SitesService} from '../shared/sites.service';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {ToastrService} from 'ngx-toastr';
import {Study} from '../../studies/shared/study.model';
import {AddSiteRequest} from '../shared/add.sites.request';
import {LocationService} from '../../location/shared/location.service';
import {ManageLocations} from '../../location/shared/location.model';
import {Observable, of} from 'rxjs';
import {Site, SiteResponse} from '../../studies/shared/site.model';
@Component({
  selector: 'add-site',
  templateUrl: './add-site.component.html',
  styleUrls: ['./add-site.component.scss'],
})
export class AddSiteComponent
  extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  @Input() study = {} as Study;
  @Output() closeModalEvent = new EventEmitter<Site>();
  @Output() cancelEvent = new EventEmitter();
  newSite = {} as Study;
  site = {} as AddSiteRequest;
  location$: Observable<ManageLocations> = of();
  disableButton = false;
  addedSite = {} as Site;
  siteName = '';
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
    this.newSite.name = this.study.name;
    this.newSite.appName = this.study.appName;
    this.site.studyId = String(this.study.id);
    this.site.locationId = '';
    this.getLocation(this.site.studyId);
  }
  getLocation(studyId: string): void {
    this.location$ = this.locationService.getLocationsForSiteCreation(studyId);
  }
  add(): void {
    this.disableButton = true;
    this.subs.add(
      this.siteService.add(this.site).subscribe(
        (successResponse: SiteResponse) => {
          this.disableButton = false;
          this.addedSite.id = successResponse.siteId;
          this.addedSite.name = successResponse.siteName;
          this.addedSite.invited = 0;
          if (getMessage(successResponse.code)) {
            this.toastr.success(getMessage(successResponse.code));
          } else {
            this.toastr.success('Success');
          }
          this.closeModal();
        },
        () => {
          this.disableButton = false;
          this.cancel();
        },
      ),
    );
  }
  closeModal(): void {
    this.closeModalEvent.emit(this.addedSite);
  }
  cancel(): void {
    this.cancelEvent.next();
  }
}
