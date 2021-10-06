import {Status} from 'src/app/shared/enums';
import {ParticipantRegistryDetail} from 'src/app/shared/participant-registry-detail';
export interface StudyDetails {
  participantRegistryDetail: ParticipantRegistryDetail;
  status: number;
  message: string;
  code: string;
  totalParticipantCount: number;
  appStatus: Status;
}
