import {Profile} from '../site-coordinator/account/shared/profile.model';
import {ApiResponse} from './api.response.model';
import {User} from './user';
export const expectedProfiledataResposnse = {
  firstName: 'Prakash',
  lastName: 'xcvxc',
  email: 'abc@grr.la',
  userId: '1',
  manageLocations: 1,
  superAdmin: true,
  status: 200,
  message: 'Get participant details successfully',
  code: 'MSG_002',
} as Profile;

export const expectedUserResposnse = {
  confirmPassword: '',
  created: '',
  createdBy: 2,
  email: 'superadmin@gmail.com',
  emailChanged: 0,
  firstName: 'kamin',
  id: '1',
  lastName: 'Dsouza',
  manageLocations: 1,
  manageUsers: 1,
  newPassword: '',
  password: '',
  phoneNumber: '0806550988',
  urAdminAuthId: 'b45bc4f67fd77ebb-34234f12a5bee975231',
} as User;

export const expectedUpdateResponse = {
  message: 'Profile upadted successfully',
  code: 'MSG_001',
} as ApiResponse;
