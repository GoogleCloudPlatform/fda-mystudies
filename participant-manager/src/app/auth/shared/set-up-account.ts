import {ApiResponse} from 'src/app/entity/api.response.model';

export interface SetUpResponse extends ApiResponse {
  userId: string;
  tempRegId: string;
}

export interface SetUpPostRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}
