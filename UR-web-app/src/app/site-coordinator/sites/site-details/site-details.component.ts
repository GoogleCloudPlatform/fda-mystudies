import {Component, OnInit, TemplateRef} from '@angular/core';
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
import {OnboardingStatus} from 'src/app/shared/enums';
import {SearchService} from 'src/app/shared/search.service';
const MAXIMUM_USER_COUNT = 10;
@Component({
  selector: 'app-site-details',
  templateUrl: './site-details.component.html',
  styleUrls: ['./site-details.component.scss'],
})
export class SiteDetailsComponent extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  query$ = new BehaviorSubject('');
  siteParticipants$: Observable<SiteParticipants> = of();
  siteDetailsBackup = {} as SiteParticipants;
  siteId = '';

  sendResend = '';
  enableDisable = '';
  toggleDisplay = false;
  userIds: string[] = [];
  onBoardingStatus = OnboardingStatus;
  activeTab = OnboardingStatus.All;

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
  openModal(templateRef: TemplateRef<unknown>): void {
    this.modalRef = this.modalService.show(templateRef);
  }
  ngOnInit(): void {
    this.sharedService.updateSearchPlaceHolder('Search Participant Email');
    this.subs.add(
      this.route.params.subscribe((params) => {
        if (params.siteId) {
          this.siteId = params.siteId as string;
        }
        this.fetchSiteParticipant(OnboardingStatus.All);
      }),
    );
  }
  toggleParticipant(): void {
    this.toggleDisplay = !this.toggleDisplay;
  }
  fetchSiteParticipant(fetchingOption: OnboardingStatus): void {
    this.siteParticipants$ = combineLatest(
      this.particpantDetailService.get(this.siteId, fetchingOption),
      this.query$,
    ).pipe(
      map(([siteDetails, query]) => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        this.siteDetailsBackup = JSON.parse(JSON.stringify(siteDetails));
        this.siteDetailsBackup.participantRegistryDetail.registryParticipants = this.siteDetailsBackup.participantRegistryDetail.registryParticipants.filter(
          (participant: RegistryParticipant) =>
            participant.email.toLowerCase().includes(query.toLowerCase()),
        );
        return this.siteDetailsBackup;
      }),
    );
  }
  search(query: string): void {
    this.query$.next(query.trim().toLowerCase());
  }
  changeTab(tab: OnboardingStatus): void {
    this.sendResend =
      tab === OnboardingStatus.New ? 'Send Invitation' : 'Resend Invitation';
    this.enableDisable =
      tab === OnboardingStatus.New || tab === OnboardingStatus.Invited
        ? 'Disable Invitation'
        : 'Enable Invitation';
    this.activeTab = tab;
    this.toggleDisplay = false;
    this.userIds = [];
    this.fetchSiteParticipant(tab);
  }
  redirectParticipant(userId: string): void {
    void this.router.navigate(['/user/participantDetail', userId]);
  }
  rowCheckBoxChange(event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    if (checkbox.checked) {
      this.userIds.push(checkbox.id);
    } else {
      this.userIds = this.userIds.filter((item) => item !== checkbox.id);
    }
  }
  decommissionSite(): void {
    this.subs.add(
      this.particpantDetailService
        .siteDecommission(this.siteId)
        .subscribe((successResponse: ApiResponse) => {
          if (getMessage(successResponse.code)) {
            this.toastr.success(getMessage(successResponse.code));
          } else {
            this.toastr.success(successResponse.message);
            void this.router.navigate(['/sites']);
          }
        }),
    );
  }
  sendInvitation(): void {
    if (this.userIds.length > 0) {
      if (this.userIds.length > MAXIMUM_USER_COUNT) {
        this.toastr.error('Please select less than 10 participants');
      } else {
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
                this.toastr.success(successResponse.message);
                this.changeTab(OnboardingStatus.Invited);
              }
            }),
        );
      }
    } else {
      this.toastr.error('Please select at least one participant');
    }
  }

  toggleInvitation(): void {
    if (this.userIds.length > 0) {
      if (this.userIds.length > MAXIMUM_USER_COUNT) {
        this.toastr.error('Please select less than 10 participants');
      } else {
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
                this.changeTab(
                  this.activeTab === OnboardingStatus.Disabled
                    ? OnboardingStatus.Disabled
                    : OnboardingStatus.New,
                );
              }
            }),
        );
      }
    } else {
      this.toastr.error('Please select at least one participant');
    }
  }

  onSucceedAddEmail(): void {
    this.modalRef.hide();
    this.fetchSiteParticipant(OnboardingStatus.New);
  }

  onFileImportSuccess(): void {
    this.fetchSiteParticipant(OnboardingStatus.New);
    this.modalRef.hide();
  }
}
