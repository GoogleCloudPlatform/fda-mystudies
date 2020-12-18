import {ApiResponse} from 'src/app/entity/api.response.model';
import {RegistryParticipant} from 'src/app/shared/participant';

export interface StatusUpdate {
  ids: string[];
  status: string;
}

export interface InviteSend {
  ids: string[];
}

export interface ConsentFile extends ApiResponse {
  version: string;
  type: string;
  content: string;
}

export interface UpdateInviteResponse extends ApiResponse {
  invitedParticipantIds: string[];
  failedParticipantIds: string[];
}

export interface Participant {
  participantDetails: RegistryParticipant;
  status: number;
  message: string;
  code: string;
}
