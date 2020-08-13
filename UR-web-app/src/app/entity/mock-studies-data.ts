import {Study} from '../site-coordinator/studies/shared/study.model';
import {StudyDetails} from '../site-coordinator/studies/shared/study-details';
import {ApiResponse} from './api.response.model';
import {UpdateTargetEnrollmentRequest} from '../site-coordinator/studies/shared/site.model';
import {AddSiteRequest} from '../site-coordinator/sites/shared/add.sites.request';
import {EnrollmentStatus} from '../shared/enums';

export const expectedStudyList = [
  {
    appId: '',
    appInfoId: 0,
    customId: 'NewStudyTest',
    enrolledCount: 41,
    enrollmentPercentage: 38,
    id: 1,
    invitedCount: 4,
    name: 'New Study Test',
    sites: [
      {
        edit: 1,
        enrolledCount: 7,
        enrollmentPercentage: 50,
        id: 11,
        invitedCount: 14,
        name: 'Location 1',
        status: '',
      },
    ],
    studyPermission: 2,
    totalSitesCount: 16,
    type: 'CLOSE',

    logo: '/path_to_img/',
  },
  {
    appId: '',
    appInfoId: 0,
    customId: 'OpenStudy',
    enrolledCount: 5,
    enrollmentPercentage: 0,
    id: 12,
    invitedCount: 9,
    name: 'Open Study 02',
    sites: [
      {
        edit: 1,
        enrolledCount: 12,
        enrollmentPercentage: 14,
        id: 10,
        invitedCount: 0,
        name: 'Location 2',
        status: '',
      },
      {
        edit: 1,
        enrolledCount: 32,
        enrollmentPercentage: 44,
        id: 10,
        invitedCount: 0,
        name: 'Location 3',
        status: '',
      },
    ],
    studyPermission: 1,
    totalSitesCount: 5,
    type: 'CLOSE',

    logo: '/path_to_img/',
  },
  {
    appId: '',
    appInfoId: 0,
    customId: 'ClosedStudy',
    enrolledCount: 54,
    enrollmentPercentage: 17,
    id: 14,
    invitedCount: 0,
    name: 'Closed Study',
    sites: [],
    studyPermission: 2,
    totalSitesCount: 6,
    type: 'OPEN',
    logo: '/path_to_img/',
  },
] as Study[];

export const expectedStudiesDetails = {
  participantRegistryDetail: {
    studyId: '24',
    customStudyId: 'CovidStudy',
    studyName: 'COVID study',
    studyType: 'OPEN',
    appId: '4028617973be410f0173be41229e0001',

    customAppId: 'mystudies-id-1',
    appName: 'mystudies-1',
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
        email: 'test12@grr.la',
        enrollmentDate: '',
        enrollmentStatus: EnrollmentStatus.Enrolled,
        id: '407',
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
  },
  status: 200,
  message: 'Get participant registry successfully',
  code: 'MSG-0013',
} as StudyDetails;

export const expectedStudyId = {id: 1} as Study;

export const expectedResponse = {
  message: 'Target Enrollment updated successfully',
} as ApiResponse;

export const expectedTargetEnrollment: UpdateTargetEnrollmentRequest = {
  targetEnrollment: 12,
};
export const expectedSiteResponse = {
  message: 'New site added successfully',
  code: 'MSG_001',
} as ApiResponse;

export const expectedNewSite = {
  studyId: '1',
  locationId: '1',
} as AddSiteRequest;
