import {EnrollmentStatus, OnboardingStatus} from './enums';

export interface RegistryParticipant {
  id: string;
  email?: string;
  enrollmentStatus: EnrollmentStatus;
  enrollmentDate: string;
  invitedDate: string;
  siteId: string;
  customLocationId: string;
  locationName?: string;
  participantRegistrySiteid?: string;
  customStudyId?: string;
  studyName?: string;
  customAppId?: string;
  appName?: string;
  onboardingStatus: OnboardingStatus;
  invitationDate?: string;
  userDetailsId?: string;
  registrationStatus?: string;
  studiesEnrolled?: string;
  registrationDate?: string;
  enrolledStudies: string[];
  enrollments: Enrollment[];
  consentHistory: Consent[];
  newlyCreatedUser: boolean;
}
export interface Site {
  siteId: string;
  customSiteId: string;
  siteName: string;
  enrollmentDate: string;
  withdrawlDate: string;
  siteStatus?: string;
}
export interface Consent {
  id: string;
  consentVersion: string;
  consentedDate: string;
  consentDocumentPath: string;
  dataSharingPermissions: string;
}
export interface Enrollment {
  participantId: string;
  withdrawalDate: string;
  enrollmentStatus: string;
  enrollmentDate: string;
}
export interface EnrolledStudy {
  studyId: string;
  customStudyId: string;
  studyName: string;
  sites: Site[];
}
