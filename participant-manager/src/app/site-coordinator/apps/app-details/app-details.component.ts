import {Component, OnInit, TemplateRef} from '@angular/core';
import {AppDetailsService} from '../shared/app-details.service';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {ActivatedRoute} from '@angular/router';
import {Observable, of, combineLatest} from 'rxjs';
import {AppDetails, EnrolledStudy} from '../shared/app-details';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {map} from 'rxjs/operators';
import {Status, StudyType} from 'src/app/shared/enums';
import {SearchService} from 'src/app/shared/search.service';

@Component({
  selector: 'app-app-details',
  templateUrl: './app-details.component.html',
  styleUrls: ['./app-details.component.scss'],
})
export class AppDetailsComponent
  extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  appId = '';
  appDetail$: Observable<AppDetails> = of();
  statusEnum = Status;
  enrolledStudies: EnrolledStudy[] = [];
  studyTypes = StudyType;
  // pagination
  limit = 10;
  currentPage = 0;
  offset = 0;
  searchTerm = '';
  sortBy: string[] | string = ['_email'];
  sortOrder = 'asc';
  constructor(
    private readonly modalService: BsModalService,
    public modalRef: BsModalRef,
    private readonly appDetailsService: AppDetailsService,
    private readonly route: ActivatedRoute,
    private readonly sharedService: SearchService,
  ) {
    super();
  }

  openModal(
    appEnrollList: TemplateRef<unknown>,
    enrolledStudies: EnrolledStudy[],
  ): void {
    this.enrolledStudies = enrolledStudies;
    if (enrolledStudies.length > 0) {
      this.modalRef = this.modalService.show(appEnrollList);
    }
  }

  ngOnInit(): void {
    this.sharedService.updateSearchPlaceHolder('Search Participant Email');
    this.subs.add(
      this.route.params.subscribe((params) => {
        if (params.appId) {
          this.appId = params.appId as string;
        }
        this.fetchParticipantsDetails();
      }),
    );
  }

  fetchParticipantsDetails(): void {
    this.appDetail$ = combineLatest(
      this.appDetailsService.get(
        this.appId,
        this.offset,
        this.limit,
        this.searchTerm,
        this.sortBy[0].replace('_', ''),
        this.sortOrder,
      ),
    ).pipe(
      map(([appDetails]) => {
        return appDetails;
      }),
    );
  }

  search(query: string): void {
    this.currentPage = 0;
    this.offset = 0;
    this.searchTerm = query.trim().toLowerCase();
    this.fetchParticipantsDetails();
  }

  pageChange(page: number): void {
    if (page >= 1) {
      this.currentPage = page;
      this.offset = (page - 1) * this.limit;
      this.fetchParticipantsDetails();
    } else if (page === 0) {
      this.currentPage = 0;
      this.offset = 0;
      this.fetchParticipantsDetails();
    }
  }

  public onSortOrder(event: string): void {
    this.sortOrder = event;
    this.offset = 0;
    this.currentPage = 0;
    this.fetchParticipantsDetails();
  }

  public onSortBy(event: string | string[]): void {
    this.sortBy = new Array(event) as string[];
    this.offset = 0;
    this.currentPage = 0;
    this.fetchParticipantsDetails();
  }
}
