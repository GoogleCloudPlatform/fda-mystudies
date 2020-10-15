import {ApiResponse} from 'src/app/entity/api.response.model';

export interface Participant {
  id: string;
  email: string;
  newlyCreatedUser: boolean;
}

export interface ImportParticipantEmailResponse extends ApiResponse {
  participants: Participant[];
  invalidEmails: string[];
  duplicateEmails: string[];
}
