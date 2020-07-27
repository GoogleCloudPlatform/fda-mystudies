import {ApiResponse} from 'src/app/entity/api.response.model';

export interface AddSite extends ApiResponse {
  studyId: string;
  locationId: string;
}
