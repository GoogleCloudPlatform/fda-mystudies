import {SuccessCodesEnum} from '../shared/success.codes.enum';

export interface ApiResponse {
  message: string;
  code: keyof typeof SuccessCodesEnum;
}
