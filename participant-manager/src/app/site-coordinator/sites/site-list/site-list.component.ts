import {Component, OnInit, TemplateRef} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
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
const limit = 10;
@Component({
  selector: 'app-site-list',
  templateUrl: './site-list.component.html',
  styleUrls: ['./site-list.component.scss'],
})
export class SiteListComponent implements OnInit {
  query$ = new BehaviorSubject('');
  study$: Observable<StudyResponse> = of();
  manageStudiesBackup = {} as StudyResponse;
  study = {} as Study;
  permission = Permission;
  studyTypes = StudyType;
  studyStatus = Status;
  searchValue = '';
  loadMoreEnabled = false;
  messageMapping: {[k: string]: string} = {
    '=0': 'No Sites',
    '=1': 'One Site',
    'other': '# Sites',
  };
  constructor(
    private readonly studiesService: StudiesService,
    private readonly modalService: BsModalService,
    private modalRef: BsModalRef,
    private readonly sharedService: SearchService,
    private readonly searchParameter: SearchParameterService,
  ) {}

  ngOnInit(): void {
    this.searchParameter.searchParam$.subscribe((updatedParameter) => {
      this.manageStudiesBackup = {} as StudyResponse;
      this.searchValue = updatedParameter;
      this.getStudies();
    });

    this.sharedService.updateSearchPlaceHolder(
      'Search by Site or Study ID or Name',
    );
  }
  closeModal(): void {
    this.modalRef.hide();
    this.getStudies();
  }

  getStudies(): void {
    this.study$ = combineLatest(
      this.studiesService.getStudiesWithSites(limit, 0, this.searchValue),
      this.query$,
    ).pipe(
      map(([manageStudies, query]) => {
        this.manageStudiesBackup = {...manageStudies};
        this.loadMoreEnabled =
          (this.manageStudiesBackup.studies.length % limit === 0
            ? true
            : false) && manageStudies.studies.length > 0;
        return this.manageStudiesBackup;
      }),
    );
  }
  search(query: string): void {
    this.query$.next(query.trim().toLowerCase());
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
    const offset = this.manageStudiesBackup.studies.length;

    this.study$ = combineLatest(
      this.studiesService.getStudiesWithSites(limit, offset, this.searchValue),
      this.query$,
    ).pipe(
      map(([manageStudies, query]) => {
        const studies = [];

        studies.push(...this.manageStudiesBackup.studies);
        studies.push(...manageStudies.studies);
        this.manageStudiesBackup.studies = studies;
        this.loadMoreEnabled =
          this.manageStudiesBackup.studies.length % limit === 0 ? true : false;

        return this.manageStudiesBackup;
      }),
    );
  }
  cancel(): void {
    this.modalRef.hide();
  }
}
