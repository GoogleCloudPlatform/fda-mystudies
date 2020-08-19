import {Site} from './site.model';

export interface Study {
  appId: string;
  appInfoId: number;
  customId: string;
  enrolledCount: number;
  enrollmentPercentage: number;
  id: number;
  invitedCount: number;
  name: string;
  sites: Site[];
  studyPermission: number;
  totalSitesCount: number;
  type: string;
  logo: string;
}
