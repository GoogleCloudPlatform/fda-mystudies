import {Permission} from 'src/app/shared/permission-enums';

export interface Site {
  siteId: string;
  locationId: string;
  customLocationId: string;
  locationName: string;
  selected: boolean;
  permission: Permission | null;
  locationDescription: string;
}

export interface Study {
  studyId: string;
  customStudyId: string;
  studyName: string;
  selected: boolean;
  permission: Permission | null;
  totalSitesCount: number;
  selectedSitesCount: number;
  sites: Site[];
}

export interface App {
  id: string;
  customId: string;
  name: string;
  permission: Permission | null;
  totalStudiesCount: number;
  totalSitesCount: number;
  selected: boolean;
  selectedSitesCount: number;
  selectedStudiesCount: number;
  studies: Study[];
}

export interface AppDetails {
  apps: App[];
  studyPermissionCount: number;
  status: number;
  message: string;
  code: string;
}
