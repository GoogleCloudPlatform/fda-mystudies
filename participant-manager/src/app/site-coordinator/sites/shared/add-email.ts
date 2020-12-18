import {ApiResponse} from 'src/app/entity/api.response.model';

export interface AddEmail {
  email: string;
}

export interface AddEmailResponse extends ApiResponse {
  participantId: string;
  newlyCreatedUser: boolean;
}
