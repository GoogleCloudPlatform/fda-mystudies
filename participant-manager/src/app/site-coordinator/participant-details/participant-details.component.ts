import {Component, OnInit} from '@angular/core';
import {Participant, UpdateInviteResponse} from './participant-details';
import {Observable, of} from 'rxjs';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {ParticipantDetailsService} from './participant-details.service';
import {ActivatedRoute} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {
  EnrollmentStatus,
  OnboardingStatus,
  Status,
  StudyType,
} from 'src/app/shared/enums';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {Location} from '@angular/common';
import {RegistryParticipant} from 'src/app/shared/participant';
import {Permission} from 'src/app/shared/permission-enums';
@Component({
  selector: 'app-participant-details',
  templateUrl: './participant-details.component.html',
  styleUrls: ['./participant-details.component.scss'],
})
export class ParticipantDetailsComponent
  extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  participantId = '';
  sendResend = '';
  enableDisable = '';
  participant$: Observable<Participant> = of();
  onBoardingStatus = OnboardingStatus;
  studyTypes = StudyType;
  permission = Permission;
  studyStatus = Status;
  enrollmentStatus = EnrollmentStatus;
  constructor(
    private readonly locationLibrary: Location,
    private readonly participantDetailsService: ParticipantDetailsService,
    private readonly route: ActivatedRoute,
    private readonly toastr: ToastrService,
  ) {
    super();
  }

  ngOnInit(): void {
    this.subs.add(
      this.route.params.subscribe((params) => {
        this.participantId = params.participantId as string;
        this.getParticipant();
      }),
    );
  }

  getParticipant(): void {
    this.participant$ = this.participantDetailsService.get(this.participantId);
    this.participant$.subscribe((participant) => {
      this.sendResend =
        participant.participantDetails.onboardingStatus ===
        this.onBoardingStatus.New
          ? 'Send Invitation'
          : 'Resend Invitation';

      this.enableDisable =
        participant.participantDetails.onboardingStatus ===
        this.onBoardingStatus.Disabled
          ? 'Enable Invitation'
          : 'Disable Invitation';
    });
  }

  downloadPDF(consentId: string): void {
    this.participantDetailsService
      .getConsentFile(consentId)
      .subscribe((consentProp) => {
        const currentTime = Date.now();
        const fileName = `consent_${currentTime}.pdf`;
        const downloadLink = document.createElement('a');
        const linkSource = `data:${consentProp.type};base64,${consentProp.content}`;
        downloadLink.href = linkSource;
        downloadLink.download = fileName;
        downloadLink.click();
      });
  }

  sendInvitation(siteId: string) {
    const sendInvitations = {
      ids: [this.participantId],
    };
    this.subs.add(
      this.participantDetailsService
        .sendInvitation(siteId, sendInvitations)
        .subscribe((successResponse: UpdateInviteResponse) => {
          if (getMessage(successResponse.code)) {
            this.toastr.success(getMessage(successResponse.code));
          } else {
            this.toastr.success('Success');
          }
          this.getParticipant();
        }),
    );
  }

  toggleInvitation(siteId: string, status: string): void {
    const statusUpdate = status === 'Disabled' ? 'N' : 'D';
    const invitationUpdate = {
      ids: [this.participantId],
      status: statusUpdate,
    };
    this.subs.add(
      this.participantDetailsService
        .toggleInvitation(siteId, invitationUpdate)
        .subscribe((successResponse: ApiResponse) => {
          if (getMessage(successResponse.code)) {
            this.toastr.success(getMessage(successResponse.code));
          } else {
            this.toastr.success('Success');
          }
          this.getParticipant();
        }),
    );
  }
  backClicked(): void {
    this.locationLibrary.back();
  }
  hasCompletedEnrollment(participantDetails: RegistryParticipant): boolean {
    return (
      participantDetails.enrollments.length > 0 &&
      participantDetails.enrollments[0].enrollmentStatus ===
        EnrollmentStatus.Enrolled
    );
  }
}
