import {ApiResponse} from './api.response.model';

export const expectedForgotEmail = {
  appId: 'PARTICIPANT-MANAGER',
  email: 'test@grr.la',
};

export const expectedForgotEmailResponse = {
  code: 'MSG_0002',
  message: 'Email has been sent to registered email address',
} as ApiResponse;
