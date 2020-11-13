import {Status} from 'src/app/shared/enums';
import {Site} from './site.model';

export interface Study {
  appId?: string;
  appInfoId?: string;
  customId?: string;
  enrollmentPercentage?: number;
  id: string;
  sitesCount: number;
  name?: string;
  appName?: string;
  sites: Site[];
  studyPermission: number;
  totalSitesCount: number;
  type: string;
  logoImageUrl: string;
  invited?: number;
  enrolled?: number;
  studyStatus: Status;
}

export interface StudyResponse {
  studies: Study[];
  sitePermissionCount: number;
  superAdmin: boolean;
  status: number;
  message: string;
  code: string;
}
