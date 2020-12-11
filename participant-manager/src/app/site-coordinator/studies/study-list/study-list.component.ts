import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {of} from 'rxjs';
import {Study, StudyResponse} from '../shared/study.model';
import {StudiesService} from '../shared/studies.service';
import {SearchService} from 'src/app/shared/search.service';
import {StudyType} from 'src/app/shared/enums';
import {Permission} from 'src/app/shared/permission-enums';
import {SearchParameterService} from 'src/app/service/search-parameter.service';
const limit = 10;
@Component({
  selector: 'app-study-list',
  templateUrl: './study-list.component.html',
  styleUrls: ['./study-list.component.scss'],
})
export class StudyListComponent implements OnInit {
  query$ = new BehaviorSubject('');
  studyList$: Observable<StudyResponse> = of();
  studies: Study[] = [];
  manageStudiesBackup = {} as StudyResponse;
  studyTypes = StudyType;
  messageMapping: {[k: string]: string} = {
    '=0': 'No Sites',
    '=1': 'One Site',
    'other': '# Sites',
  };
  loadMoreEnabled = true;
  searchValue = '';
  constructor(
    private readonly studiesService: StudiesService,
    private readonly router: Router,
    private readonly toastr: ToastrService,
    private readonly sharedService: SearchService,
    private readonly searchParameter: SearchParameterService,
  ) {}

  ngOnInit(): void {
    this.searchParameter.searchParam$.subscribe((upadtedParameter) => {
      this.manageStudiesBackup = {} as StudyResponse;
      this.searchValue = upadtedParameter;
      this.getStudies();
    });

    this.sharedService.updateSearchPlaceHolder('Search By Study ID or Name');
  }

  getStudies(): void {
    this.studyList$ = combineLatest(
      this.studiesService.getStudies(limit, 0, this.searchValue),
      this.query$,
    ).pipe(
      map(([manageStudies, query]) => {
        this.manageStudiesBackup = {...manageStudies};
        if (
          !manageStudies.superAdmin &&
          manageStudies.sitePermissionCount < 2
        ) {
          this.toastr.error(
            'This view displays study-wise enrollment if you manage multiple sites.',
          );
        }

        this.manageStudiesBackup.studies = this.manageStudiesBackup.studies.filter(
          (study: Study) =>
            study.name?.toLowerCase().includes(query.toLowerCase()) ||
            study.customId?.toLowerCase().includes(query.toLowerCase()),
        );
        this.loadMoreEnabled =
          (this.manageStudiesBackup.studies.length % limit === 0
            ? true
            : false) && manageStudies.studies.length > 0;

        return this.manageStudiesBackup;
      }),
    );
  }

  search(query: string): void {
    this.query$.next(query.trim());
  }
  progressBarColor(study: Study): string {
    if (study.enrollmentPercentage && study.enrollmentPercentage > 70) {
      return 'green__text__sm';
    } else if (
      study.enrollmentPercentage &&
      study.enrollmentPercentage >= 30 &&
      study.enrollmentPercentage <= 70
    ) {
      return 'orange__text__sm';
    } else {
      return 'red__text__sm';
    }
  }

  loadMoreSites() {
    const offset = this.manageStudiesBackup.studies.length;

    this.studyList$ = combineLatest(
      this.studiesService.getStudies(limit, offset, this.searchValue),
      this.query$,
    ).pipe(
      map(([manageStudies, query]) => {
        const studies = [];

        studies.push(...this.manageStudiesBackup.studies);
        studies.push(...manageStudies.studies);
        this.manageStudiesBackup.studies = studies;
        this.manageStudiesBackup.studies = this.manageStudiesBackup.studies.filter(
          (study: Study) =>
            study.name?.toLowerCase().includes(query.toLowerCase()) ||
            study.customId?.toLowerCase().includes(query.toLowerCase()),
        );
        this.loadMoreEnabled =
          this.manageStudiesBackup.studies.length % limit === 0 ? true : false;
        return this.manageStudiesBackup;
      }),
    );
  }

  checkViewPermission(permission: number): boolean {
    return (
      permission === Permission.View || permission === Permission.ViewAndEdit
    );
  }
}
