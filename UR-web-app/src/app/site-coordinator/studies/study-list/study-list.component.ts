import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {ApiResponse} from 'src/app/entity/error.model';
import {throwError, BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {of} from 'rxjs';
import {Study} from '../shared/study.model';
import {StudiesService} from '../shared/studies.service';
@Component({
  selector: 'app-study-list',
  templateUrl: './study-list.component.html',
  styleUrls: ['./study-list.component.scss'],
})
export class StudyListComponent implements OnInit {
  query$ = new BehaviorSubject('');
  study$: Observable<Study[]> = of([]);
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
    this.study$ = combineLatest(
      this.studiesService.getStudies(),
      this.query$,
    ).pipe(
      catchError((error: ApiResponse) => {
        this.toastr.error(error.error.userMessage);
        return throwError(error);
      }),
      map(([studies, query]) => {
        this.studies = studies;
        return this.studies.filter(
          (study: Study) =>
            (study.name &&
              study.name.toLowerCase().includes(query.toLowerCase())) ||
            (study.customId &&
              study.customId.toLowerCase().includes(query.toLowerCase())),
        );
      }),
    );
  }
  search(query: string): void {
    this.query$.next(query.trim());
  }
}
