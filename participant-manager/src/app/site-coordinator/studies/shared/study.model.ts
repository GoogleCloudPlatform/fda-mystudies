import {Site} from './site.model';

export interface Study {
  appId?: string;
  appInfoId?: string;
  customId: string;
  enrollmentPercentage?: number;
  id: string;
  sitesCount: number;
  name: string;
  sites: Site[];
  studyPermission: number;
  totalSitesCount: number;
  type: string;
  logo: string;
  invited?: number;
  enrolled?: number;
}

export interface StudyResponse {
  studies: Study[];
  sitePermissionCount: number;
  status: number;
  message: string;
  code: string;
}
