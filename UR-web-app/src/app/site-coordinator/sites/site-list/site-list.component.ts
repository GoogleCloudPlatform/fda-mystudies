import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {of} from 'rxjs';
import {Study} from '../../studies/shared/study.model';
import {SitesService} from '../shared/sites.service';
import {Site} from '../../studies/shared/site.model';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {LocationService} from '../../location/shared/location.service';
import {Location} from '../../location/shared/location.model';
import {StudiesService} from '../../studies/shared/studies.service';
import {AddSite} from '../shared/add.sites.model';
import {getMessage, SuccessCodesEnum} from 'src/app/shared/success.codes.enum';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';

@Component({
  selector: 'app-site-list',
  templateUrl: './site-list.component.html',
  styleUrls: ['./site-list.component.scss'],
})
export class SiteListComponent extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  query$ = new BehaviorSubject('');
  study$: Observable<Study[]> = of([]);
  newSite = {} as Study;
  site = {} as AddSite;
  locations: Location[] = [];
  messageMapping: {[k: string]: string} = {
    '=0': 'No Sites',
    '=1': 'One Site',
    'other': '# Sites',
  };
  constructor(
    private readonly studiesService: StudiesService,
    private readonly siteService: SitesService,
    private readonly locationService: LocationService,
    private readonly router: Router,
    private readonly toastr: ToastrService,
    private readonly modalService: BsModalService,
    private modalRef: BsModalRef,
  ) {
    super();
  }

  ngOnInit(): void {
    this.getStudies();
  }

  getStudies(): void {
    this.study$ = combineLatest(
      this.studiesService.getStudiesWithSites(),
      this.query$,
    ).pipe(
      map(([studies, query]) => {
        return studies.filter(
          (study: Study) =>
            study.name.toLowerCase().includes(query.toLowerCase()) ||
            study.customId.toLowerCase().includes(query.toLowerCase()),
        );
      }),
    );
  }
  search(query: string): void {
    this.query$.next(query.trim());
  }
  progressBarColor(site: Site): string {
    if (site.enrollmentPercentage < 30) {
      return 'red__text__sm';
    } else if (site.enrollmentPercentage < 70) {
      return 'orange__text__sm';
    } else {
      return 'green__text__sm';
    }
  }
  openAddSiteModal(template: unknown, study: Study): void {
    this.modalRef = this.modalService.show(template);
    this.getLocation(study.id);
    this.newSite.customId = study.customId;
    this.newSite.appId = study.appId;
    this.site.studyId = study.id.toString();
  }
  getLocation(studyId: number): void {
    if (studyId) {
      this.subs.add(
        this.locationService
          .getLocationsForSiteCreation(studyId.toString())
          .subscribe((data) => {
            this.locations = data;
          }),
      );
    }
  }
  addSite(): void {
    this.subs.add(
      this.siteService.addSite(this.site).subscribe(
        (successResponse: AddSite) => {
          if (
            getMessage(successResponse.code as keyof typeof SuccessCodesEnum)
          ) {
            this.toastr.success(
              getMessage(successResponse.code as keyof typeof SuccessCodesEnum),
            );
          } else {
            this.toastr.success('Success');
          }
          this.modalRef.hide();
          this.getStudies();
        },
        () => {
          this.modalRef.hide();
        },
      ),
    );
  }
}
