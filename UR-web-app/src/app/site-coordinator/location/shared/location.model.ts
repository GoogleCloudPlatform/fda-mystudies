import {ApiSuccessResponse} from 'src/app/entity/sucess.model';
import {ApiResponse} from 'src/app/entity/error.model';

export interface Location extends ApiSuccessResponse, ApiResponse {
  id: number;

  name: string;

  customId: string;

  description: string;

  status: string;

  studiesCount: number;
}
