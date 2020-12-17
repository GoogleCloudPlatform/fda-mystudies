import {Injectable} from '@angular/core';
import {SiteParticipants} from './site-detail.model';
import {EntityService} from '../../../service/entity.service';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '@environment';
import {AddEmail, AddEmailResponse} from './add-email';
import {
  InviteSend,
  StatusUpdate,
  UpdateInviteResponse,
} from '../../participant-details/participant-details';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {OnboardingStatus} from 'src/app/shared/enums';
import {ImportParticipantEmailResponse} from './import-participants';
@Injectable({
  providedIn: 'root',
})
export class SiteDetailsService {
  baseUrl = environment.participantManagerDatastoreUrl;
  constructor(
    private readonly entityService: EntityService<SiteParticipants>,
    private readonly http: HttpClient,
  ) {}

  get(
    siteId: string,
    fetchingOption: OnboardingStatus,
  ): Observable<SiteParticipants> {
    const fetchingOptions = this.getInvitationType(fetchingOption);
    if (fetchingOption !== OnboardingStatus.All) {
      return this.http.get<SiteParticipants>(
        `${
          environment.participantManagerDatastoreUrl
        }/sites/${encodeURIComponent(siteId)}/participants`,
        {
          params: {
            onboardingStatus: fetchingOptions,
          },
        },
      );
    } else {
      return this.http.get<SiteParticipants>(
        `${
          environment.participantManagerDatastoreUrl
        }/sites/${encodeURIComponent(siteId)}/participants`,
      );
    }
  }

  siteDecommission(siteId: string): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(
      `${environment.participantManagerDatastoreUrl}/sites/${encodeURIComponent(
        siteId,
      )}/decommission`,
      '',
    );
  }

  toggleInvitation(
    siteId: string,
    participantToBeUpdated: StatusUpdate,
  ): Observable<ApiResponse> {
    return this.http.patch<ApiResponse>(
      `${environment.participantManagerDatastoreUrl}/sites/${encodeURIComponent(
        siteId,
      )}/participants/status`,
      participantToBeUpdated,
    );
  }

  sendInvitation(
    siteId: string,
    invitationToSend: InviteSend,
  ): Observable<UpdateInviteResponse> {
    return this.http.post<UpdateInviteResponse>(
      `${environment.participantManagerDatastoreUrl}/sites/${encodeURIComponent(
        siteId,
      )}/participants/invite`,
      invitationToSend,
    );
  }
  addParticipants(
    siteId: string,
    modelEmail: AddEmail,
  ): Observable<AddEmailResponse> {
    return this.http.post<AddEmailResponse>(
      `${environment.participantManagerDatastoreUrl}/sites/${encodeURIComponent(
        siteId,
      )}/participants`,
      modelEmail,
    );
  }

  private getInvitationType(fetchingOption: OnboardingStatus): string {
    switch (fetchingOption) {
      case OnboardingStatus.New:
        return 'N';
      case OnboardingStatus.Invited:
        return 'I';
      case OnboardingStatus.Disabled:
        return 'D';
      default:
        return '';
    }
  }

  importParticipants(
    siteId: string,
    formData: FormData,
  ): Observable<ImportParticipantEmailResponse> {
    return this.http.post<ImportParticipantEmailResponse>(
      `${environment.participantManagerDatastoreUrl}/sites/${encodeURIComponent(
        siteId,
      )}/participants/import`,
      formData,
      {headers: {skipIfUpload: 'true'}},
    );
  }
}
