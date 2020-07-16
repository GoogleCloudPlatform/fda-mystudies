import {ApiResponse} from 'src/app/entity/api.response.model';

export interface Location extends ApiResponse {
  id: number;

  name: string;

  customId: string;

  description: string;

  status: string;

  studiesCount: number;
}
