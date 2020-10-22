import {
  Participant,
  StatusUpdate,
  InviteSend,
  UpdateInviteResponse,
} from '../site-coordinator/participant-details/participant-details';
import {ApiResponse} from './api.response.model';
import {RegistryParticipant} from '../shared/participant';
import { EnrollmentStatus } from '../shared/enums';

export const expectedParticipantDetails = {
  participantDetails: {
    id: '1',
    email: 'test@grr.la',
    enrollmentStatus: EnrollmentStatus.YetToEnroll,
    enrollmentDate: '28/06/1952',
    invitedDate: '28/06/1952',
    siteId: 'LocaA',
    customLocationId: 'OpenStudy02',
    locationName: 'Marlborough',
    participantRegistrySiteid: '402880a073d71bff0173d71c16d90009',
    customStudyId: 'CovidStudy',
    studyName: 'COVID Study',
    customAppId: 'MyStudies-Id-1',
    appName: 'MyStudies-1',
    onboardingStatus: 'New',
    invitationDate: '08/10/2020',
    userDetailsId: 'dssad',
    registrationStatus: 'Registered',
    studiesEnrolled: '2',
    registrationDate: '28/06/1952',
    enrolledStudies: [],
    enrollments: [
      {
        participantId: '23',
        withdrawalDate: '08/10/2020',
        enrollmentStatus: EnrollmentStatus.YetToEnroll,
        enrollmentDate: '08/10/2020',
      },
    ],
    consentHistory: [
      {
        id: '402880a073d71bff0173d71c16e6000b',
        consentVersion: '1.0',
        consentedDate: '08/10/2020',
        consentDocumentPath: 'documents/test-document.pdf',
        dataSharingPermissions: '1',
      },
    ],
  },
  status: 200,
  message: 'Get participant details successfully',
  code: 'MSG_0020',
} as Participant;

export const expectedParticipantId = {
  id: '1',
} as RegistryParticipant;

export const expectedSiteId = {siteId: '1'} as RegistryParticipant;

export const expectedToggleResponse = {
  message: 'Site status updated successfully',
} as ApiResponse;

export const expectedToggleInvitation = {
  ids: ['1'],
  status: 'D',
} as StatusUpdate;
export const expectedDecommissionResponse = {
  message: 'Site decomissioned successfully',
} as ApiResponse;
export const expectedSendInvitation = {ids: ['1']} as InviteSend;

export const expectedSendInviteResponse = {
  invitedParticipantIds: ['1'],
  failedParticipantIds: [''],
} as UpdateInviteResponse;
