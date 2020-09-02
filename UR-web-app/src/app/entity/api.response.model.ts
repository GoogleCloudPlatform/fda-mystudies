import {SuccessCode} from '../shared/success.codes.enum';
import {ErrorCode} from '../shared/error.codes.enum';

export interface ApiResponse {
  message: string;
  code: SuccessCode;
  /* eslint-disable @typescript-eslint/naming-convention */
  /* eslint-disable camelcase */
  error_code: ErrorCode;
}
