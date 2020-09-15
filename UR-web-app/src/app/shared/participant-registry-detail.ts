/* eslint-disable @typescript-eslint/naming-convention */
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
  siteStatus?: number;
  registryParticipants: RegistryParticipant[];
  countByStatus: CountByStatus;
  status?: string;
}
export interface CountByStatus {
  A?: number;
  D?: number;
  E?: number;
  I?: number;
  N?: number;
}
