import {ApiResponse} from 'src/app/entity/api.response.model';

export interface Site {
  edit?: number;
  enrolled: number;
  enrollmentPercentage: number;
  status?: string;
  id: string;
  name?: string;
  invited?: number;
}
export interface UpdateTargetEnrollmentRequest {
  targetEnrollment: number;
}
export interface SiteResponse extends ApiResponse {
  siteId: string;
  siteName: string;
}
