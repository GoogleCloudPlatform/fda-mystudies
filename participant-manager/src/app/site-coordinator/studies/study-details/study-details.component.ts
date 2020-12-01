import {Component, OnInit} from '@angular/core';
import {StudyDetailsService} from '../shared/study-details.service';
import {ActivatedRoute} from '@angular/router';
import {BehaviorSubject, Observable, of, combineLatest} from 'rxjs';
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
import {RegistryParticipant} from 'src/app/shared/participant';
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
  query$ = new BehaviorSubject('');
  studyDetail$: Observable<StudyDetails> = of();
  studyDetailsBackup = {} as StudyDetails;
  studyTypes = StudyType;
  onboardingStatus = OnboardingStatus;
  enrollmentStatus = EnrollmentStatus;
  permission = Permission;
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
    console.log('study details');
    this.sharedService.updateSearchPlaceHolder(
      'Search by Site ID or Participant Email',
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
      this.studyDetailsService.getStudyDetails(this.studyId),
      this.query$,
    ).pipe(
      map(([studyDetails, query]) => {
        this.studyDetailsBackup = JSON.parse(
          JSON.stringify(studyDetails),
        ) as StudyDetails;
        if (
          this.studyDetailsBackup.participantRegistryDetail.studyType ===
            StudyType.Open &&
          query === ''
        ) {
          this.sharedService.updateSearchPlaceHolder(
            'Search Participant Email',
          );
        }
        this.studyDetailsBackup.participantRegistryDetail.registryParticipants = this.studyDetailsBackup.participantRegistryDetail.registryParticipants.filter(
          (participant: RegistryParticipant) =>
            participant.enrollmentStatus !== EnrollmentStatus.NotEligile &&
            (participant.email?.toLowerCase().includes(query.toLowerCase()) ||
              participant.locationName
                ?.toLowerCase()
                .includes(query.toLowerCase())),
        );
        return this.studyDetailsBackup;
      }),
    );
  }
  search(query: string): void {
    this.query$.next(query.trim());
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
