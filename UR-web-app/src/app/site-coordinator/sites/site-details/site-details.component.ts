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
@Component({
  selector: 'app-site-details',
  templateUrl: './site-details.component.html',
  styleUrls: ['./site-details.component.scss'],
})
export class SiteDetailsComponent extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  constructor(
    private readonly particpantDetailService: SiteDetailsService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly toastr: ToastrService,
    private readonly modalService: BsModalService,
    public modalRef: BsModalRef,
  ) {
    super();
  }
  query$ = new BehaviorSubject('');
  siteParticipants$: Observable<SiteParticipants> = of();
  siteId = '';
  sendResend = '';
  enableDisable = '';
  activeTab = 'all';
  toggLeDisplay = false;
  arrayOfuserId: string[] = [];
  onBoardingStatus = OnboardingStatus;
  openModal(templateref: TemplateRef<unknown>): void {
    this.modalRef = this.modalService.show(templateref);
  }
  ngOnInit(): void {
    this.subs.add(
      this.route.params.subscribe((params) => {
        if (params.siteId) {
          this.siteId = params.siteId as string;
        }
        this.fetchSiteParticipant('all');
      }),
    );
  }
  toggleParticipant(): void {
    if (this.toggLeDisplay) {
      this.toggLeDisplay = false;
    } else this.toggLeDisplay = true;
  }
  fetchSiteParticipant(fetcingOption: string): void {
    this.siteParticipants$ = combineLatest(
      this.particpantDetailService.get(this.siteId, fetcingOption),
      this.query$,
    ).pipe(
      map(([siteDetails, query]) => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        siteDetails.participantRegistryDetail.registryParticipants = siteDetails.participantRegistryDetail.registryParticipants.filter(
          (participant: RegistryParticipant) =>
            participant.email.toLowerCase().includes(query.toLowerCase()),
        );
        return siteDetails;
      }),
    );
  }
  search(query: string): void {
    this.query$.next(query.trim());
  }
  changeTab(tab: string): void {
    this.sendResend = tab === 'new' ? 'Send Invitation' : 'Resend Invitation';
    this.enableDisable =
      tab === 'new' || tab === 'inivited'
        ? 'Disable Invitation'
        : 'Enable Invitation';
    this.activeTab = tab;
    this.toggLeDisplay = false;
    this.arrayOfuserId.splice(0, this.arrayOfuserId.length);
    this.fetchSiteParticipant(this.activeTab);
  }
  redirectParticipant(userId: string): void {
    void this.router.navigate(['/user/participantDetail', userId]);
  }
  rowCheckBoxChange(event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    if (checkbox.checked) {
      this.arrayOfuserId.push(checkbox.id);
    } else {
      this.arrayOfuserId = this.arrayOfuserId.filter(
        (item) => item !== checkbox.id,
      );
    }
    console.log(this.arrayOfuserId);
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
    if (this.arrayOfuserId.length > 0) {
      if (this.arrayOfuserId.length > 11) {
        this.toastr.error('Please select less than 10 participants');
      } else {
        const sendInvitations = {
          ids: this.arrayOfuserId,
        };
        this.subs.add(
          this.particpantDetailService
            .sendInvitation(this.siteId, sendInvitations)
            .subscribe((successResponse: UpdateInviteResponse) => {
              if (getMessage(successResponse.code)) {
                this.toastr.success(getMessage(successResponse.code));
              } else {
                this.toastr.success(successResponse.message);
                this.changeTab('invited');
              }
            }),
        );
      }
    } else {
      this.toastr.error(
        'Please select atleast one participant for sending invitation',
      );
    }
  }
  toggleInvitation(): void {
    if (this.arrayOfuserId.length > 0) {
      if (this.arrayOfuserId.length > 11) {
        this.toastr.error('Please select less than 10 participants');
      } else {
        const statusUpdate =
          this.activeTab === 'Enable' || this.activeTab === 'Invited'
            ? 'N'
            : 'D';
        const invitationUpdate = {
          ids: this.arrayOfuserId,
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
                this.changeTab(status === 'enable' ? 'new' : 'disabled');
              }
            }),
        );
      }
    } else {
      this.toastr.error(
        status === '0'
          ? 'Please select atleast one participant to disable'
          : 'Please select atleast one participant for to enable',
      );
    }
  }
  onSucceedAddEmail(): void {
    this.modalRef.hide();
    this.fetchSiteParticipant(this.activeTab);
  }
  onSucceedFileImport(): void {
    this.changeTab('new');
    this.modalRef.hide();
  }
}
