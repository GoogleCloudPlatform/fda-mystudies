import {Injectable} from '@angular/core';
import {
  Participant,
  StatusUpdate,
  UpdateInviteResponse,
  InviteSend,
  ConsentFile,
} from './participant-details';
import {EntityService} from 'src/app/service/entity.service';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '@environment';
import {ApiResponse} from 'src/app/entity/api.response.model';

@Injectable({
  providedIn: 'root',
})
export class ParticipantDetailsService {
  constructor(
    private readonly entityService: EntityService<Participant>,
    private readonly http: HttpClient,
  ) {}

  get(participantId: string): Observable<Participant> {
    return this.entityService.get(
      `sites/${encodeURIComponent(participantId)}/participant`,
    );
  }

  toggleInvitation(
    siteId: string,
    participantToBeUpdated: StatusUpdate,
  ): Observable<ApiResponse> {
    return this.http.patch<ApiResponse>(
      `${environment.baseUrl}/sites/${encodeURIComponent(
        siteId,
      )}/participants/activate`,
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

  getConsentFile(consentId: string): Observable<ConsentFile> {
    return this.http.get<ConsentFile>(
      `${environment.baseUrl}/consents/${encodeURIComponent(
        consentId,
      )}/consentDocument`,
    );
  }
}
