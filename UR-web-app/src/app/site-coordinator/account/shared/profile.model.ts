import {ApiResponse} from 'src/app/entity/api.response.model';
export interface Profile extends ApiResponse {
  firstName: string;
  lastName: string;
  email: string;
}
export interface UpdateProfile {
  firstName: string;
  lastName: string;
}

export interface ChangePassword {
  currentPassword: string;
  newPassword: string;
}
