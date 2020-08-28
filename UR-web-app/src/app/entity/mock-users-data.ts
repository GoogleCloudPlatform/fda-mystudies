import {ManageUsers} from '../site-coordinator/user/shared/manage-user';

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
