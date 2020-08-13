import {RegistryParticipant} from './participant';
import {PermisssionEnum} from './permission.enums';
import {StudyTypes} from './enums';

export interface ParticipantRegistryDetail {
  studyId: string;
  customStudyId: string;
  studyType: StudyTypes;
  targetEnrollment: number;
  studyName: string;
  appId: string;
  customAppId: string;
  appName: string;
  siteId?: string;
  customLocationId?: string;
  locationName?: string;
  locationStatus?: string;
  sitePermission?: PermisssionEnum;
  openStudySitePermission?: PermisssionEnum;
  siteStatus?: string;
  registryParticipants: RegistryParticipant[];
  countByStatus?: string;
  status?: string;
}
