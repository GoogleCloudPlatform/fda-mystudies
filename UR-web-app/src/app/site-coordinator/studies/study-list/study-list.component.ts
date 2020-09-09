import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {of} from 'rxjs';
import {Study} from '../shared/study.model';
import {StudiesService} from '../shared/studies.service';
import {SearchService} from 'src/app/shared/search.service';
@Component({
  selector: 'app-study-list',
  templateUrl: './study-list.component.html',
  styleUrls: ['./study-list.component.scss'],
})
export class StudyListComponent implements OnInit {
  query$ = new BehaviorSubject('');
  study$: Observable<Study[]> = of([]);
  studies: Study[] = [];
  manageStudiesBackup = {} as StudyResponse;

  constructor(
    private readonly studiesService: StudiesService,
    private readonly router: Router,
    private readonly toastr: ToastrService,
    private readonly sharedService: SearchService,
  ) {}

  ngOnInit(): void {
    this.sharedService.updateSearchPlaceHolder('Search By Study ID or Name');
    this.getStudies();
  }

  getStudies(): void {
    this.study$ = combineLatest(
      this.studiesService.getStudies(),
      this.query$,
    ).pipe(
      map(([manageStudies, query]) => {
        this.manageStudiesBackup = {...manageStudies};
        this.manageStudiesBackup.studies = this.manageStudiesBackup.studies.filter(
          (study: Study) =>
            study.name.toLowerCase().includes(query.toLowerCase()) ||
            study.customId.toLowerCase().includes(query.toLowerCase()),
        );
        return this.manageStudiesBackup;
      }),
    );
  }
  search(query: string): void {
    this.query$.next(query.trim());
  }
  progressBarColor(study: Study): string {
    if (study.enrollmentPercentage < 30) {
      return 'red__text__sm';
    } else if (study.enrollmentPercentage < 70) {
      return 'orange__text__sm';
    } else {
      return 'green__text__sm';
    }
  }
}
