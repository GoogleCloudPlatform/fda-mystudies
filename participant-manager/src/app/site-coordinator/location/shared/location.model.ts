import {ApiResponse} from 'src/app/entity/api.response.model';
import {SuccessCode} from 'src/app/shared/success.codes.enum';

export interface ManageLocations {
  locations: Location[];
}
export interface Location extends ApiResponse {
  locationId: string;

  name?: string;

  customId?: string;

  description: string;

  status: number;

  studyNames: string[];

  studiesCount: number;
}

export interface StatusUpdateRequest {
  status: number;
}

export interface FieldUpdateRequest {
  name?: string;
  description: string;
}

export interface UpdateLocationResponse {
  description: string;
  name: string;
  status: number;
  code: SuccessCode;
}
