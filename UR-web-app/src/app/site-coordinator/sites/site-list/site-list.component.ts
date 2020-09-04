import {Component, OnInit, TemplateRef} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {of} from 'rxjs';
import {Study, StudyResponse} from '../../studies/shared/study.model';
import {Site} from '../../studies/shared/site.model';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {StudiesService} from '../../studies/shared/studies.service';

@Component({
  selector: 'app-site-list',
  templateUrl: './site-list.component.html',
  styleUrls: ['./site-list.component.scss'],
})
export class SiteListComponent implements OnInit {
  query$ = new BehaviorSubject('');
  study$: Observable<StudyResponse> = of();
  study = {} as Study;
  messageMapping: {[k: string]: string} = {
    '=0': 'No Sites',
    '=1': 'One Site',
    'other': '# Sites',
  };
  constructor(
    private readonly studiesService: StudiesService,
    private readonly modalService: BsModalService,
    private modalRef: BsModalRef,
  ) {}

  ngOnInit(): void {
    this.getStudies();
  }
  closeModal(): void {
    this.modalRef.hide();
    this.getStudies();
  }
  getStudies(): void {
    this.study$ = combineLatest(
      this.studiesService.getStudiesWithSites(),
      this.query$,
    ).pipe(
      map(([studies, query]) => {
        studies.studies.filter((study: Study) =>
          study.name.toLowerCase().includes(query.toLowerCase()),
        );
        return studies;
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
  openAddSiteModal(template: TemplateRef<unknown>, study: Study): void {
    this.modalRef = this.modalService.show(template);
    this.study = study;
  }
}
