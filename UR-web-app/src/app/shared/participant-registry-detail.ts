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
  // eslint-disable-next-line @typescript-eslint/naming-convention
  A?: number;
  // eslint-disable-next-line @typescript-eslint/naming-convention
  D?: number;
  // eslint-disable-next-line @typescript-eslint/naming-convention
  E?: number;
  // eslint-disable-next-line @typescript-eslint/naming-convention
  I?: number;
  // eslint-disable-next-line @typescript-eslint/naming-convention
  N?: number;
}
