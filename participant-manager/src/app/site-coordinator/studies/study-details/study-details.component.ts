import {Component, OnInit} from '@angular/core';
import {StudyDetailsService} from '../shared/study-details.service';
import {ActivatedRoute} from '@angular/router';
import {Observable, of, combineLatest} from 'rxjs';
import {StudyDetails} from '../shared/study-details';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {map} from 'rxjs/operators';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {
  StudyType,
  OnboardingStatus,
  EnrollmentStatus,
} from 'src/app/shared/enums';
import {Permission} from 'src/app/shared/permission-enums';
import {TemplateRef} from '@angular/core';
import {SearchService} from 'src/app/shared/search.service';
import {Location} from '@angular/common';
@Component({
  selector: 'app-study-details',
  templateUrl: './study-details.component.html',
  styleUrls: ['./study-details.component.scss'],
})
export class StudyDetailsComponent
  extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  studyId = '';
  studyDetail$: Observable<StudyDetails> = of();
  studyTypes = StudyType;
  onboardingStatus = OnboardingStatus;
  enrollmentStatus = EnrollmentStatus;
  permission = Permission;
  // pagination
  limit = 10;
  currentPage = 1;
  offset = 0;
  searchTerm = '';
  sortBy: string[] | string = ['email'];
  sortOrder = 'asc';
  constructor(
    private readonly locationLibrary: Location,
    private readonly modalService: BsModalService,
    private modalRef: BsModalRef,
    private readonly studyDetailsService: StudyDetailsService,
    private readonly route: ActivatedRoute,
    private readonly sharedService: SearchService,
  ) {
    super();
  }

  ngOnInit(): void {
    this.sharedService.updateSearchPlaceHolder(
      'Search by Site or Participant Email',
    );

    this.subs.add(
      this.route.params.subscribe((params) => {
        if (params['studyId']) {
          this.studyId = params.studyId as string;
        }
        this.getStudyDetails();
      }),
    );
  }
  getStudyDetails(): void {
    this.studyDetail$ = combineLatest(
      this.studyDetailsService.getStudyDetails(
        this.studyId,
        this.offset,
        this.limit,
        this.searchTerm,
        this.sortBy[0],
        this.sortOrder,
      ),
    ).pipe(
      map(([studyDetails]) => {
        if (
          studyDetails.participantRegistryDetail.studyType === StudyType.Open
        ) {
          this.sharedService.updateSearchPlaceHolder(
            'Search Participant Email',
          );
        }
        return studyDetails;
      }),
    );
  }

  search(query: string): void {
    this.currentPage = 1;
    this.offset = 0;
    this.searchTerm = query.trim().toLowerCase();
    this.getStudyDetails();
  }

  pageChange(page: number): void {
    this.currentPage = page;
    this.offset = (page - 1) * this.limit;
    this.getStudyDetails();
  }

  public onSortOrder(event: string): void {
    this.sortOrder = event;
    this.offset = 0;
    this.currentPage = 0;
    this.getStudyDetails();
  }

  public onSortBy(event: string | string[]): void {
    this.sortBy = new Array(event) as string[];
    this.offset = 0;
    this.currentPage = 0;
    this.getStudyDetails();
  }

  openModal(template: TemplateRef<unknown>): void {
    this.modalRef = this.modalService.show(template);
  }

  closeModal(targetEnrollment: number): void {
    this.studyDetail$ = this.studyDetail$.pipe(
      map((studyDetails) => {
        studyDetails.participantRegistryDetail.targetEnrollment = targetEnrollment;
        return studyDetails;
      }),
    );
    this.modalRef.hide();
  }

  backClicked(): void {
    this.locationLibrary.back();
  }
}
