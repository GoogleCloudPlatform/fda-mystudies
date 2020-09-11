import {Injectable} from '@angular/core';
import {SiteParticipants} from './site-detail.model';
import {EntityService} from '../../../service/entity.service';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '@environment';
import {AddEmail} from './add-email';
import {
  InviteSend,
  StatusUpdate,
  UpdateInviteResponse,
} from '../../participant-details/participant-details';
import {ApiResponse} from 'src/app/entity/api.response.model';

@Injectable({
  providedIn: 'root',
})
export class SiteDetailsService {
  baseUrl = environment.baseUrl;
  constructor(
    private readonly entityService: EntityService<SiteParticipants>,
    private readonly http: HttpClient,
  ) {}

  get(siteId: string, fetchingOption: string): Observable<SiteParticipants> {
    const fetchingOptions =
      fetchingOption === 'all'
        ? ''
        : fetchingOption === 'new'
        ? '?onboardingStatus=N'
        : fetchingOption === 'invited'
        ? '?onboardingStatus=I'
        : '?onboardingStatus=D';
    return this.entityService.get(
      `sites/${siteId}/participants` + fetchingOptions,
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
  addParticipants(
    siteId: string,
    modelEmail: AddEmail,
  ): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${environment.baseUrl}/sites/${encodeURIComponent(siteId)}/participants`,
      modelEmail,
    );
  }
}
