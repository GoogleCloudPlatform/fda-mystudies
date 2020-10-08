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
import {StudyType} from 'src/app/shared/enums';

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
  ) {}

  ngOnInit(): void {
    this.sharedService.updateSearchPlaceHolder(
      'Search By Site or Study ID or Name',
    );
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
      map(([manageStudies, query]) => {
        this.manageStudiesBackup = {...manageStudies};
        this.manageStudiesBackup.studies = this.manageStudiesBackup.studies.filter(
          (study: Study) =>
            study.name?.toLowerCase().includes(query) ||
            study.customId?.toLowerCase().includes(query) ||
            study.sites.some((site) => site.name?.includes(query)),
        );
        return this.manageStudiesBackup;
      }),
    );
  }
  search(query: string): void {
    this.query$.next(query.trim().toLowerCase());
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
