import {Permission} from 'src/app/shared/permission-enums';

export interface Profile {
  firstName: string;
  lastName: string;
  phoneNum: string;
  email: string;
  userId: string;
  manageLocations: Permission | null;
  superAdmin: boolean;
  status: 200;
  message: string;
  code: string;
  idpUser:boolean;
  
}
export interface UpdateProfile {
  firstName: string;
  lastName: string;
  phoneNum: string;
}

export interface ChangePassword {
  currentPassword: string;
  newPassword: string;
}
