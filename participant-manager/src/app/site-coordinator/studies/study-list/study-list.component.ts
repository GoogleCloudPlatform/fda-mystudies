import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {of} from 'rxjs';
import {Study, StudyResponse} from '../shared/study.model';
import {StudiesService} from '../shared/studies.service';
import {SearchService} from 'src/app/shared/search.service';
import {Status, StudyType} from 'src/app/shared/enums';
import {Permission} from 'src/app/shared/permission-enums';
import {SearchParameterService} from 'src/app/service/search-parameter.service';
const limit = 10;
@Component({
  selector: 'app-study-list',
  templateUrl: './study-list.component.html',
  styleUrls: ['./study-list.component.scss'],
})
export class StudyListComponent implements OnInit {
  studyList$: Observable<StudyResponse> = of();
  studiesDisplay: Study[] = [];
  manageStudiesBackup = {} as StudyResponse;
  studyTypes = StudyType;
  messageMapping: {[k: string]: string} = {
    '=0': 'No sites',
    '=1': 'One site',
    'other': '# sites',
  };
  loadMoreEnabled = true;
  searchValue = '';
  studyStatus = Status;

  constructor(
    private readonly studiesService: StudiesService,
    private readonly router: Router,
    private readonly toastr: ToastrService,
    private readonly sharedService: SearchService,
    private readonly searchParameter: SearchParameterService,
  ) {}

  ngOnInit(): void {
    this.searchParameter.setSearchParameter('');
    this.searchParameter.searchParam$.subscribe((updatedParameter) => {
      this.manageStudiesBackup = {} as StudyResponse;
      this.searchValue = updatedParameter;
      this.getStudies();
    });

    this.sharedService.updateSearchPlaceHolder('Search by study ID or name');
  }

  getStudies(): void {
    this.studyList$ = combineLatest(
      this.studiesService.getStudies(limit, 0, this.searchValue),
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

  loadMoreSites(): void {
    const offset = this.manageStudiesBackup.studies.length;
    this.studiesService
      .getStudies(limit, offset, this.searchValue)
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

  checkViewPermission(permission: number): boolean {
    return (
      permission === Permission.View || permission === Permission.ViewAndEdit
    );
  }
}
