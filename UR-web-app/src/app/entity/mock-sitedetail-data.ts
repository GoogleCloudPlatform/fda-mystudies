import {EnrollmentStatus} from '../shared/enums';
import {
  StatusUpdate,
  InviteSend,
  UpdateInviteResponse,
} from '../site-coordinator/participant-details/participant-details';
import {ApiResponse} from './api.response.model';
import {RegistryParticipant} from '../shared/participant';
import {SiteParticipants} from '../site-coordinator/sites/shared/site-detail.model';

export const expectedSiteParticipantDetails = {
  participantRegistryDetail: {
    studyId: '2',
    customStudyId: 'TestStudy002',
    studyName: 'pqr',
    studyType: 'OPEN',
    appId: 'Studies',
    customAppId: 'Studies',
    appName: 'My-Studies',
    siteId: '2',
    customLocationId: 'Location1',
    locationName: 'Location1',
    targetEnrollment: 1,
    registryParticipants: [
      {
        customLocationId: '',
        email: 'test1@grr.la',
        enrollmentDate: '',
        enrollmentStatus: EnrollmentStatus.Enrolled,
        id: '408',
        invitedDate: '06/05/2020',
        locationName: '',
        onboardingStatus: 'Invited',
        siteId: '0',
        enrolledStudies: [],
        enrollments: [],
        consentHistory: [],
      },
      {
        customLocationId: '',
        email: 'test123@grr.la',
        enrollmentDate: '',
        enrollmentStatus: EnrollmentStatus.Enrolled,
        id: '406',
        invitedDate: '06/05/2020',
        locationName: '',
        onboardingStatus: 'Invited',
        siteId: '0',
        enrolledStudies: [],
        enrollments: [],
        consentHistory: [],
      },
    ],

    countByStatus: {
      // eslint-disable-next-line @typescript-eslint/naming-convention
      A: 1,
      // eslint-disable-next-line @typescript-eslint/naming-convention
      D: 0,
      // eslint-disable-next-line @typescript-eslint/naming-convention
      E: 1,
      // eslint-disable-next-line @typescript-eslint/naming-convention
      I: 12,
      // eslint-disable-next-line @typescript-eslint/naming-convention
      N: 21,
    },
  },
  status: 200,
  message: 'Get participant registry successfully',
  code: 'MSG-0013',
} as SiteParticipants;

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

export const expectedSendInvitation = {ids: ['408', '409']} as InviteSend;

export const expectedSendInviteResponse = {
  invitedParticipantIds: ['1'],
  failedParticipantIds: [''],
} as UpdateInviteResponse;

export const expectedImportedEmailListResponse = {
  message: 'Email imported successfully',
} as ApiResponse;
