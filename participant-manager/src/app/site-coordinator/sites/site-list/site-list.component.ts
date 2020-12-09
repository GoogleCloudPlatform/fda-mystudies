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
import {SearchTermService} from 'src/app/service/search-term.service';

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
  loadMoreEnabled = true;
  limit = 10;
  searchValue = '';
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
    private readonly searchTerm: SearchTermService,
  ) {}

  ngOnInit(): void {
    this.searchTerm.searchParameter$.subscribe((upadtedUsername) => {
      if (upadtedUsername === '') {
        this.getStudies();
      } else {
        this.manageStudiesBackup = {} as StudyResponse;
        this.searchValue = upadtedUsername;
        this.searchParameter();
      }
    });

    this.sharedService.updateSearchPlaceHolder(
      'Search by Site or Study ID or Name',
    );
  }
  closeModal(): void {
    this.modalRef.hide();
    this.getStudies();
  }

  cancel(): void {
    this.modalRef.hide();
  }

  getStudies(): void {
    this.study$ = combineLatest(
      this.studiesService.getStudiesWithSites(this.limit, 0),
      this.query$,
    ).pipe(
      map(([manageStudies, query]) => {
        this.manageStudiesBackup = {...manageStudies};
        this.manageStudiesBackup.studies = this.manageStudiesBackup.studies.filter(
          (study: Study) =>
            study.name?.toLowerCase().includes(query) ||
            study.customId?.toLowerCase().includes(query) ||
            study.sites.some((site) =>
              site.name?.toLowerCase()?.includes(query),
            ),
        );
        this.loadMoreEnabled =
          this.manageStudiesBackup.studies.length % this.limit === 0
            ? true
            : false;
        console.log(this.manageStudiesBackup.studies.length);
        return this.manageStudiesBackup;
      }),
    );
  }
  // search(query: string): void {
  //   this.query$.next(query.trim().toLowerCase());
  // }
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
      this.studiesService.searchStudiesWithSites(
        this.limit,
        offset,
        this.searchValue,
      ),
      this.query$,
    ).pipe(
      map(([manageStudies, query]) => {
        const studies = [];

        studies.push(...this.manageStudiesBackup.studies);
        studies.push(...manageStudies.studies);

        this.manageStudiesBackup.studies = studies;
        this.manageStudiesBackup.studies = this.manageStudiesBackup.studies.filter(
          (study: Study) =>
            study.name?.toLowerCase().includes(query) ||
            study.customId?.toLowerCase().includes(query) ||
            study.sites.some((site) =>
              site.name?.toLowerCase()?.includes(query),
            ),
        );

        this.loadMoreEnabled =
          this.manageStudiesBackup.studies.length % this.limit === 0
            ? true
            : false;

        return this.manageStudiesBackup;
      }),
    );
  }

  searchParameter(): void {
    this.loadMoreEnabled = false;
    // const offset = this.manageStudiesBackup.studies.length;
    this.study$ = combineLatest(
      this.studiesService.searchStudiesWithSites(
        this.limit,
        0,
        this.searchValue,
      ),
      this.query$,
    ).pipe(
      map(([manageStudies, query]) => {
        this.manageStudiesBackup = {...manageStudies};
        this.manageStudiesBackup.studies = this.manageStudiesBackup.studies.filter(
          (study: Study) =>
            study.name?.toLowerCase().includes(query) ||
            study.customId?.toLowerCase().includes(query) ||
            study.sites.some((site) =>
              site.name?.toLowerCase()?.includes(query),
            ),
        );
        this.loadMoreEnabled =
          this.manageStudiesBackup.studies.length % this.limit === 0
            ? true
            : false;
        console.log(this.manageStudiesBackup.studies.length);
        return this.manageStudiesBackup;
      }),
    );
  }
}
