import {ParticipantRegistryDetail} from '../../../shared/participant-registry-detail';

export interface SiteParticipants {
  participantRegistryDetail: ParticipantRegistryDetail;
  status: number;
  message: string;
  code: string;
}
