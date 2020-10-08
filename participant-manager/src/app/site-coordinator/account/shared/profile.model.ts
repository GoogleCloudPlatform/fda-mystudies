export interface Profile {
  firstName: string;
  lastName: string;
  email: string;
  userId: string;
  manageLocations: number;
  superAdmin: boolean;
  status: 200;
  message: string;
  code: string;
}
export interface UpdateProfile {
  firstName: string;
  lastName: string;
}

export interface ChangePassword {
  currentPassword: string;
  newPassword: string;
}
