import {App} from '../site-coordinator/user/shared/app-details';
import {Permission} from '../shared/permission-enums';
import {Status} from '../shared/enums';
import {ApiResponse} from './api.response.model';

export interface User extends ApiResponse {
  id: string;

  email?: string;

  firstName?: string;

  lastName?: string;

  phoneNumber: string;

  emailChanged: number;

  status?: Status;

  manageUsers: number;

  manageLocations: Permission | null;

  superAdmin: boolean;

  urAdminAuthId: string;

  created: string;

  createdBy: number;

  newPassword: string;

  password: string;

  confirmPassword: string;

  appId: string;

  userId: string;

  tempRegId: string;

  authUserId: string;

  apps: App[];

  manageLocationsSelected?: boolean;

    redirectTo?:string;

}

export interface SetUpUser {
  email: string;
  firstName: string;
  lastName: string;
  password: string;
}
