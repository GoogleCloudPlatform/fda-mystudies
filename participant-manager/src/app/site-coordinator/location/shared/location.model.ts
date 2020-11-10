import {ApiResponse} from 'src/app/entity/api.response.model';
import {Permission} from 'src/app/shared/permission-enums';
import {SuccessCode} from 'src/app/shared/success.codes.enum';

export interface ManageLocations {
  locations: Location[];
  locationPermission:Permission;

}
export interface Location extends ApiResponse {
  locationId: string;

  name: string;

  customId: string;

  description: string;

  status: number;

  studyNames: string[];

  studiesCount: number;

  locationPermission:Permission;


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
