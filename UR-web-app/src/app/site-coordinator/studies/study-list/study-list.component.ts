import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {of} from 'rxjs';
import {Study, StudyResponse} from '../shared/study.model';
import {StudiesService} from '../shared/studies.service';
@Component({
  selector: 'app-study-list',
  templateUrl: './study-list.component.html',
  styleUrls: ['./study-list.component.scss'],
})
export class StudyListComponent implements OnInit {
  query$ = new BehaviorSubject('');
  studyList$: Observable<StudyResponse> = of();
  studies: Study[] = [];
  constructor(
    private readonly studiesService: StudiesService,
    private readonly router: Router,
    private readonly toastr: ToastrService,
  ) {}

  ngOnInit(): void {
    this.getStudies();
  }

  getStudies(): void {
    this.studyList$ = combineLatest(
      this.studiesService.getStudies(),
      this.query$,
    ).pipe(
      map(([study, query]) => {
        this.studies = study.studies;
        study.studies = study.studies.filter(
          (study: Study) =>
            study.name.toLowerCase().includes(query.toLowerCase()) ||
            study.customId.toLowerCase().includes(query.toLowerCase()),
        );
        return study;
      }),
    );
  }
  search(query: string): void {
    this.query$.next(query.trim());
  }
  progressBarColor(study: Study): string {
    if (study.enrollmentPercentage && study.enrollmentPercentage < 30) {
      return 'red__text__sm';
    } else if (study.enrollmentPercentage && study.enrollmentPercentage < 70) {
      return 'orange__text__sm';
    } else {
      return 'green__text__sm';
    }
  }
}
