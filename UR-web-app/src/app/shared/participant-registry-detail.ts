import {RegistryParticipant} from './participant';
import {Permission} from './permission-enums';
import {StudyType} from './enums';

export interface ParticipantRegistryDetail {
  studyId: string;
  customStudyId: string;
  studyType: StudyType;
  targetEnrollment: number;
  studyName: string;
  appId: string;
  customAppId: string;
  appName: string;
  siteId?: string;
  customLocationId?: string;
  locationName?: string;
  locationStatus?: string;
  sitePermission?: Permission;
  openStudySitePermission?: Permission;
  siteStatus?: string;
  registryParticipants: RegistryParticipant[];
  countByStatus?: string;
  status?: string;
}
