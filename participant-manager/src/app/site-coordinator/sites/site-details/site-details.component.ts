import {Component, OnInit, TemplateRef, HostListener} from '@angular/core';
import {SiteParticipants} from '../shared/site-detail.model';
import {Router, ActivatedRoute} from '@angular/router';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {ToastrService} from 'ngx-toastr';
import {BehaviorSubject, Observable, of, combineLatest} from 'rxjs';
import {SiteDetailsService} from '../shared/site-details.service';
import {RegistryParticipant} from 'src/app/shared/participant';
import {map} from 'rxjs/operators';
import {UpdateInviteResponse} from '../../participant-details/participant-details';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {EnrollmentStatus, OnboardingStatus, Status} from 'src/app/shared/enums';
import {SearchService} from 'src/app/shared/search.service';
import {
  ImportParticipantEmailResponse,
  Participant,
} from '../shared/import-participants';
import {Permission} from 'src/app/shared/permission-enums';
import {ParticipantRegistryDetail} from 'src/app/shared/participant-registry-detail';

@Component({
  selector: 'app-site-details',
  templateUrl: './site-details.component.html',
  styleUrls: ['./site-details.component.scss'],
})
export class SiteDetailsComponent
  extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  query$ = new BehaviorSubject('');
  siteParticipants$: Observable<SiteParticipants> = of();
  siteDetailsBackup = {} as SiteParticipants;
  siteId = '';
  permission = Permission;
  sendResend = '';
  enableDisable = '';
  toggleDisplay = false;
  userIds: string[] = [];
  onBoardingStatus = OnboardingStatus;
  activeTab = OnboardingStatus.All;
  newlyImportedParticipants: Participant[] = [];
  selectedAll = false;
  studyStatus = Status;
  enrollmentStatus = EnrollmentStatus;
  invitedYetToEnrollCount = 0;
  userIdsBackup: string[] = [];
  activeTabForDisabled = '';

  constructor(
    private readonly particpantDetailService: SiteDetailsService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly toastr: ToastrService,
    private readonly modalService: BsModalService,
    public modalRef: BsModalRef,
    private readonly sharedService: SearchService,
  ) {
    super();
  }

  @HostListener('click') onClick() {
    this.toggleDisplay = false;
  }

  openModal(templateRef: TemplateRef<unknown>): void {
    this.modalRef = this.modalService.show(templateRef);
  }
  ngOnInit(): void {
    this.sharedService.updateSearchPlaceHolder('Search participant email');
    this.subs.add(
      this.route.params.subscribe((params) => {
        if (params.siteId) {
          this.siteId = params.siteId as string;
        }
        this.fetchSiteParticipant(OnboardingStatus.All);
      }),
    );
  }
  toggleParticipant($event: Event): void {
    $event.stopPropagation();
    this.toggleDisplay = !this.toggleDisplay;
  }
  fetchSiteParticipant(fetchingOption: OnboardingStatus): void {
    this.siteParticipants$ = combineLatest(
      this.particpantDetailService.get(this.siteId, fetchingOption),
      this.query$,
    ).pipe(
      map(([siteDetails, query]) => {
        this.siteDetailsBackup = JSON.parse(
          JSON.stringify(siteDetails),
        ) as SiteParticipants;

        this.siteDetailsBackup.participantRegistryDetail.registryParticipants.map(
          (participant) => {
            if (this.activeTabForDisabled === OnboardingStatus.Disabled) {
              const resultFromDisabled = this.userIds.filter(
                (idsFromDisabled) => idsFromDisabled === participant.id,
              );
              if (resultFromDisabled.length > 0) {
                participant.newlyCreatedUser = true;
              }
            } else {
              const result = this.newlyImportedParticipants.filter(
                (newlyVreatedEmails) =>
                  newlyVreatedEmails.email === participant.email,
              );
              if (result.length > 0) {
                participant.newlyCreatedUser = true;
              }
            }
            return participant;
          },
        );

        this.invitedYetToEnrollCount = this.siteDetailsBackup.participantRegistryDetail.registryParticipants.filter(
          function (participant) {
            return (
              participant.enrollmentStatus === EnrollmentStatus.YetToEnroll
            );
          },
        ).length;

        this.newlyImportedParticipants = [];
        this.siteDetailsBackup.participantRegistryDetail.registryParticipants = this.siteDetailsBackup.participantRegistryDetail.registryParticipants.filter(
          (participant: RegistryParticipant) =>
            participant.email?.toLowerCase().includes(query.toLowerCase()),
        );
        return this.siteDetailsBackup;
      }),
    );
  }
  search(query: string): void {
    this.query$.next(query.trim().toLowerCase());
  }
  changeTab(tab: OnboardingStatus): void {
    this.selectedAll = false;
    this.sendResend =
      tab === OnboardingStatus.New ? 'Send invitation' : 'Resend invitation';
    this.enableDisable =
      tab === OnboardingStatus.New || tab === OnboardingStatus.Invited
        ? 'Disable invitation'
        : 'Enable invitation';
    this.activeTabForDisabled = this.activeTab;
    this.activeTab = tab;
    this.toggleDisplay = false;
    this.userIdsBackup = this.userIds;
    this.userIds = [];
    if (this.activeTabForDisabled === OnboardingStatus.Disabled) {
      this.userIds = this.userIdsBackup;
    }
    this.fetchSiteParticipant(tab);
  }

  rowCheckBoxChange(event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    if (checkbox.checked) {
      this.userIds.push(checkbox.id);
    } else {
      this.selectedAll = false;
      this.userIds = this.userIds.filter((item) => item !== checkbox.id);
    }
  }
  decommissionSite(participantRegistryDetail: ParticipantRegistryDetail): void {
    this.subs.add(
      this.particpantDetailService
        .siteDecommission(this.siteId)
        .subscribe((successResponse: ApiResponse) => {
          participantRegistryDetail.siteStatus =
            participantRegistryDetail.siteStatus === 1 ? 0 : 1;
          if (getMessage(successResponse.code)) {
            this.toastr.success(getMessage(successResponse.code));
          } else {
            this.toastr.success('success');
          }
          this.fetchSiteParticipant(this.activeTab);
        }),
    );
  }
  sendInvitation(): void {
    if (this.userIds.length > 0) {
      const sendInvitations = {
        ids: this.userIds,
      };
      this.subs.add(
        this.particpantDetailService
          .sendInvitation(this.siteId, sendInvitations)
          .subscribe((successResponse: UpdateInviteResponse) => {
            if (getMessage(successResponse.code)) {
              this.toastr.success(getMessage(successResponse.code));
            } else {
              this.toastr.success('success');
            }
            this.changeTab(OnboardingStatus.Invited);
          }),
      );
    } else {
      this.toastr.error('Please select at least one participant');
    }
  }

  toggleInvitation(): void {
    if (this.userIds.length > 0) {
      const statusUpdate =
        this.activeTab === OnboardingStatus.Disabled ? 'N' : 'D';
      const invitationUpdate = {
        ids: this.userIds,
        status: statusUpdate,
      };
      this.subs.add(
        this.particpantDetailService
          .toggleInvitation(this.siteId, invitationUpdate)
          .subscribe((successResponse: ApiResponse) => {
            if (getMessage(successResponse.code)) {
              this.toastr.success(getMessage(successResponse.code));
            } else {
              this.toastr.success(successResponse.message);
            }
            this.changeTab(
              this.activeTab !== OnboardingStatus.Disabled
                ? OnboardingStatus.Disabled
                : OnboardingStatus.New,
            );
          }),
      );
    } else {
      this.toastr.error('Please select at least one participant');
    }
  }

  onSucceedAddEmail(event: Participant[]): void {
    this.userIds = [];
    this.newlyImportedParticipants = event;
    this.newlyImportedParticipants.map((newlyCreatedparticpants) =>
      this.userIds.push(newlyCreatedparticpants.id),
    );
    this.modalRef.hide();
    this.sendResend = 'Send invitation';
    this.enableDisable = 'Disable invitation';
    this.activeTab = OnboardingStatus.New;
    this.toggleDisplay = false;
    this.fetchSiteParticipant(OnboardingStatus.New);
  }

  onFileImportSuccess(event: ImportParticipantEmailResponse): void {
    this.userIds = [];
    this.newlyImportedParticipants = event.participants;
    this.newlyImportedParticipants.map((newlyCreatedparticpants) =>
      this.userIds.push(newlyCreatedparticpants.id),
    );
    this.modalRef.hide();
    this.sendResend = 'Send invitation';
    this.enableDisable = 'Disable invitation';
    this.activeTab = OnboardingStatus.New;
    this.toggleDisplay = false;
    this.fetchSiteParticipant(OnboardingStatus.New);
  }

  selectAll(): void {
    this.userIds = [];
    if (this.selectedAll) {
      for (const participants of this.siteDetailsBackup
        .participantRegistryDetail.registryParticipants) {
        if (this.activeTab === OnboardingStatus.Invited) {
          if (
            participants.enrollmentStatus === EnrollmentStatus.YetToEnroll ||
            participants.enrollmentStatus === EnrollmentStatus.NotEligile
          ) {
            participants.newlyCreatedUser = this.selectedAll;
            this.userIds.push(participants.id);
          }
        } else {
          participants.newlyCreatedUser = this.selectedAll;
          this.userIds.push(participants.id);
        }
      }
    } else {
      for (const participants of this.siteDetailsBackup
        .participantRegistryDetail.registryParticipants) {
        participants.newlyCreatedUser = this.selectedAll;
      }
    }
  }
}
