import {App} from '../site-coordinator/user/shared/app-details';
import {Permission} from '../shared/permission-enums';
import {Status} from '../shared/enums';

export interface User {
  id: string;

  email: string;

  firstName: string;

  lastName: string;

  phoneNumber: string;

  emailChanged: number;

  status: Status;

  manageUsers: number;

  manageLocations: Permission | null;

  superAdmin: boolean;

  urAdminAuthId: string;

  created: string;

  createdBy: number;

  newPassword: string;

  password: string;

  confirmPassword: string;

  apps: App[];

  manageLocationsSelected: boolean;
}
