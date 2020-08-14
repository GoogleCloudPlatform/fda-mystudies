import {ParticipantRegistryDetail} from 'src/app/shared/participant-registry-detail';

export interface StudyDetails {
  participantRegistryDetail: ParticipantRegistryDetail;
  status: number;
  message: string;
  code: string;
}
