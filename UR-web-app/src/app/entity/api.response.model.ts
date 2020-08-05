import {SuccessCode} from '../shared/success.codes.enum';

export interface ApiResponse {
  message: string;
  code: SuccessCode;
}
