import {ApiResponse} from 'src/app/entity/api.response.model';
import {SuccessCode} from 'src/app/shared/success.codes.enum';

export interface Location extends ApiResponse {
  id: number;

  name: string;

  customId: string;

  description: string;

  status: string;

  studiesCount: number;
}
export interface StatusUpdateRequest {
  status: string;
}

export interface FieldUpdateRequest {
  name: string;
  description: string;
}

export interface UpdateLocationResponse {
  description: string;
  name: string;
  status: string;
  code: SuccessCode;
}
