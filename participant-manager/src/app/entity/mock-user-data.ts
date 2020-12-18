import {ApiResponse} from './api.response.model';
import {SetUpUser, User} from './user';

export const expectedForgotEmail = {
  appId: 'PARTICIPANT-MANAGER',
  email: 'test@grr.la',
};

export const expectedForgotEmailResponse = {
  code: 'MSG_0002',
  message: 'Email has been sent to registered email address',
} as ApiResponse;

export const expectedUserDetails = {
  email: 'superadmin@grr.la',
  firstName: 'kamin',
  lastName: 'Dsouza',
  password: '',
} as User;

export const expectedSetUpCode = {code: 'wR4RMz7BGMNNXf6H9lWjV'};

export const expectedsetUpResponse = {
  userId: '1',
  tempRegId: '7fd50c2c-d618-493c-89d6-f1887e3e4bb8',
} as User;

export const expectedUpdateSetUp = {
  email: 'test@grr.la',
  password: 'Boston@123',
  firstName: 'Chiranjibi',
  lastName: 'Dash',
} as SetUpUser;
