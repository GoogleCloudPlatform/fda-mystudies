import {Component, OnInit, TemplateRef} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {of} from 'rxjs';
import {Study, StudyResponse} from '../../studies/shared/study.model';
import {Site} from '../../studies/shared/site.model';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {StudiesService} from '../../studies/shared/studies.service';
import {SearchService} from 'src/app/shared/search.service';
import {Permission} from 'src/app/shared/permission-enums';
import {Status, StudyType} from 'src/app/shared/enums';
import {SearchParameterService} from 'src/app/service/search-parameter.service';
import {ViewportScroller} from '@angular/common';
const limit = 10;
@Component({
  selector: 'app-site-list',
  templateUrl: './site-list.component.html',
  styleUrls: ['./site-list.component.scss'],
})
export class SiteListComponent implements OnInit {
  study$: Observable<StudyResponse> = of();
  manageStudiesBackup = {} as StudyResponse;
  studiesDisplay: Study[] = [];
  study = {} as Study;
  permission = Permission;
  studyTypes = StudyType;
  studyStatus = Status;
  searchValue = '';
  loadMoreEnabled = false;
  messageMapping: {[k: string]: string} = {
    '=0': 'No sites',
    '=1': 'One site',
    'other': '# sites',
  };
  constructor(
    private readonly studiesService: StudiesService,
    private readonly modalService: BsModalService,
    private modalRef: BsModalRef,
    private readonly sharedService: SearchService,
    private readonly searchParameter: SearchParameterService,
    private readonly viewportScroller: ViewportScroller,
  ) {
    this.viewportScroller.setHistoryScrollRestoration('manual');
  }

  ngOnInit(): void {
    this.viewportScroller.setHistoryScrollRestoration('manual');
    this.searchParameter.setSearchParameter('');
    this.searchParameter.searchParam$.subscribe((updatedParameter) => {
      this.manageStudiesBackup = {} as StudyResponse;
      this.searchValue = updatedParameter;
      this.getStudies();
    });

    this.sharedService.updateSearchPlaceHolder(
      'Search by site or study ID or name',
    );
  }
  closeModal(event: Site): void {
    this.study.sites.push(event);
    this.study.sites.sort((site1, site2): number => {
      if (site1.name !== undefined && site2.name !== undefined) {
        if (site1.name.toLowerCase() < site2.name.toLowerCase()) return -1;
        if (site1.name.toLowerCase() > site2.name.toLowerCase()) return 1;
      }
      return 0;
    });
    this.modalRef.hide();
  }

  getStudies(): void {
    this.study$ = combineLatest(
      this.studiesService.getStudiesWithSites(limit, 0, this.searchValue),
    ).pipe(
      map(([manageStudies]) => {
        const studies = [];
        this.manageStudiesBackup = {...manageStudies};
        studies.push(...manageStudies.studies);
        this.studiesDisplay = studies;
        this.loadMoreEnabled =
          (this.manageStudiesBackup.studies.length % limit === 0
            ? true
            : false) && manageStudies.studies.length > 0;
        return this.manageStudiesBackup;
      }),
    );
  }
  progressBarColor(site: Site): string {
    if (site.enrollmentPercentage && site.enrollmentPercentage > 70) {
      return 'green__text__sm';
    } else if (
      site.enrollmentPercentage &&
      site.enrollmentPercentage >= 30 &&
      site.enrollmentPercentage <= 70
    ) {
      return 'orange__text__sm';
    } else {
      return 'red__text__sm';
    }
  }
  openAddSiteModal(template: TemplateRef<unknown>, study: Study): void {
    this.modalRef = this.modalService.show(template);
    this.study = study;
  }

  loadMoreSites(): void {
    console.log(this.viewportScroller.getScrollPosition());
    const offset = this.manageStudiesBackup.studies.length;
    this.studiesService
      .getStudiesWithSites(limit, offset, this.searchValue)
      .subscribe((manageStudies) => {
        const studies = [];
        studies.push(...this.manageStudiesBackup.studies);
        studies.push(...manageStudies.studies);
        this.studiesDisplay = studies;
        this.manageStudiesBackup.studies = studies;
        this.loadMoreEnabled =
          (this.manageStudiesBackup.studies.length % limit === 0
            ? true
            : false) && manageStudies.studies.length > 0;
      });
  }
  cancel(): void {
    this.modalRef.hide();
  }
}
