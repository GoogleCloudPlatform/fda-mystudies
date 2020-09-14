import {Injectable} from '@angular/core';
import {SiteParticipants} from './site-detail.model';
import {EntityService} from '../../../service/entity.service';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '@environment';
import {
  InviteSend,
  StatusUpdate,
  UpdateInviteResponse,
} from '../../participant-details/participant-details';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {OnboardingStatus} from 'src/app/shared/enums';
@Injectable({
  providedIn: 'root',
})
export class SiteDetailsService {
  baseUrl = environment.baseUrl;
  constructor(
    private readonly entityService: EntityService<SiteParticipants>,
    private readonly http: HttpClient,
  ) {}
  get(
    siteId: string,
    fetchingOption: OnboardingStatus,
  ): Observable<SiteParticipants> {
    const fetchingOptions = this.getInvitationType(fetchingOption);
    return this.entityService.get(
      `sites/${encodeURIComponent(siteId)}/participants` + fetchingOptions,
    );
  }

  siteDecommission(siteId: string): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(
      `${environment.baseUrl}/sites/${encodeURIComponent(siteId)}/decommission`,
      '',
    );
  }
  toggleInvitation(
    siteId: string,
    participantToBeUpdated: StatusUpdate,
  ): Observable<ApiResponse> {
    return this.http.patch<ApiResponse>(
      `${environment.baseUrl}/sites/${encodeURIComponent(
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
      `${environment.baseUrl}/sites/${encodeURIComponent(
        siteId,
      )}/participants/invite`,
      invitationToSend,
    );
  }

  getInvitationType(fetchingOption: string): string {
    switch (fetchingOption) {
      case 'All':
        return '';
        break;

      case 'New':
        return '?onboardingStatus=N';
        break;

      case 'Invited':
        return '?onboardingStatus=I';
        break;

      case 'Disabled':
        return '?onboardingStatus=D';
        break;
    }
    return '';
  }
}
