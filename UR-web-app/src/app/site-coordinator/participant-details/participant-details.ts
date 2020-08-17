import {ApiResponse} from 'src/app/entity/api.response.model';

export interface StatusUpdate {
  ids: string[];
  status: string;
}

export interface InviteSend {
  ids: string[];
}

export interface ConsentFile extends ApiResponse {
  version: string;
  type: string;
  content: string;
}

export interface UpdateInviteResponse extends ApiResponse {
  invitedParticipantIds: string[];
  failedParticipantIds: string[];
}

export interface Enrollment {
  participantId: string;
  withdrawalDate: string;
  enrollmentStatus: string;
  enrollmentDate: string;
}

export interface ConsentHistory {
  id: string;
  consentVersion: string;
  consentedDate: string;
  consentDocumentPath: string;
  dataSharingPermissions: string;
}

export interface Site {
  siteId: string;
  customSiteId: string;
  siteName: string;
  enrollmentDate: string;
  withdrawlDate: string;
  siteStatus?: string;
}
export interface EnrolledStudy {
  studyId: string;
  customStudyId: string;
  studyName: string;
  sites: Site[];
}
export interface ParticipantDetail {
  id: string;
  email: string;
  enrollmentStatus: string;
  enrollmentDate: string;
  invitedDate: string;
  siteId: string;
  customLocationId: string;
  locationName: string;
  participantRegistrySiteid: string;
  customStudyId: string;
  studyName: string;
  customAppId: string;
  appName: string;
  onboardingStatus: string;
  invitationDate: string;
  userDetailsId: string;
  registrationStatus: string;
  studiesEnrolled: string;
  registrationDate: string;
  enrolledStudies: EnrolledStudy[];
  enrollments: Enrollment[];
  consentHistory: ConsentHistory[];
}

export interface Participant {
  participantDetail: ParticipantDetail;
  status: number;
  message: string;
  code: string;
}
