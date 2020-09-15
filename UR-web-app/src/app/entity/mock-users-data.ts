import {ManageUsers} from '../site-coordinator/user/shared/manage-user';
import {ManageUserDetails} from '../site-coordinator/user/shared/manage-user-details';

export const expectedManageUsers = {
  users: [
    {
      firstName: 'john',
      lastName: 'kin',
      email: 'john@grr.la',
      superAdmin: false,
      manageLocations: 1,
    },
    {
      firstName: 'lara',
      lastName: 'datta',
      email: 'lara@grr.la',
      superAdmin: true,
      manageLocations: 1,
    },
  ],
} as ManageUsers;
export const expectedManageUserDetails = {
  user: {
    firstName: 'john',
    lastName: 'kin',
    email: 'john@grr.la',
    superAdmin: false,
    manageLocations: 1,
  },
} as ManageUserDetails;
