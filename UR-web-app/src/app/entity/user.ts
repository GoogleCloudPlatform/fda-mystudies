export interface User {
  id: number;

  email: string;

  firstName: string;

  lastName: string;

  phoneNumber: string;

  emailChanged: number;

  status: number;

  manageUsers: number;

  manageLocations: number;

  urAdminAuthId: string;

  created: string;

  createdBy: number;

  newPassword: string;

  password: string;

  confirmPassword: string;
}
