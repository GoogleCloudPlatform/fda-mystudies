export interface AppDetails {
  appId: string;
  customId: string;
  name: string;
  participants: Participant[];
  status: number;
  message: string;
  code: string;
}

export interface Participant {
  email?: string;
  userDetailsId: string;
  registrationStatus: string;
  registrationDate: string;
  enrolledStudies: EnrolledStudy[];
  enrollments: Enrollment[];
  consentHistory: Consent[];
}

export interface EnrolledStudy {
  studyId: string;
  customStudyId: string;
  studyName: string;
  sites: Site[];
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
