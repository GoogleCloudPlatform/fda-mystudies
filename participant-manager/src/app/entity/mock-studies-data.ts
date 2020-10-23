/* eslint-disable @typescript-eslint/naming-convention */
import {
  Study,
  StudyResponse,
} from '../site-coordinator/studies/shared/study.model';
import {StudyDetails} from '../site-coordinator/studies/shared/study-details';
import {ApiResponse} from './api.response.model';
import {UpdateTargetEnrollmentRequest} from '../site-coordinator/studies/shared/site.model';
import {AddSiteRequest} from '../site-coordinator/sites/shared/add.sites.request';
import {EnrollmentStatus, StudyType} from '../shared/enums';

export const expectedSitesList = {
  studies: [
    {
      id: '1',
      customId: 'TestStudy001',
      name: 'abcd',
      sitesCount: 1,
      sites: [
        {
          id: '1',
          name: 'Location1',
          invited: 300,
          enrolled: 1,
          enrollmentPercentage: 0.3333333333333333,
          edit: 1,
          status: '',
        },
      ],
      type: 'OPEN',
      appInfoId: '1',
      appId: 'Studies',
      studyPermission: 1,
    },
    {
      id: '3',
      customId: 'TestStudy003',
      name: 'abcd',
      sitesCount: 1,
      sites: [
        {
          id: '5',
          name: 'Location1',
          invited: 0,
          enrolled: null,
          enrollmentPercentage: null,
          edit: null,
          status: null,
        },
      ],
      type: 'CLOSE',
      appInfoId: '1',
      appId: 'Studies',
      studyPermission: 2,
    },
  ],
  status: 200,
  message: 'Get sites successfully',
  code: 'MSG-0018',
} as StudyResponse;

export const expectedStudyList = {
  studies: [
    {
      id: '5',
      customId: 'test1',
      name: 'abcd',
      sitesCount: 1,
      type: 'CLOSE',
      invited: 0,
      enrolled: 0,
    },
    {
      id: '3',
      customId: 'TestStudy003',
      name: 'abcd',
      sitesCount: 1,
      type: 'CLOSE',
      invited: 0,
      enrolled: 0,
      studyPermission: 1,
    },
    {
      id: '1',
      customId: 'TestStudy001',
      name: 'abcd',
      sitesCount: 1,
      type: 'OPEN',
      invited: 300,
      enrolled: 1,
      enrollmentPercentage: 0.3333333333333333,
      studyPermission: 1,
    },
    {
      id: '2',
      customId: 'TestStudy002',
      name: 'pqr',
      sitesCount: 13,
      type: 'CLOSE',
      invited: 28,
      enrolled: 4,
      enrollmentPercentage: 14.285714285714286,
      studyPermission: 2,
    },
  ],
  sitePermissionCount: 16,
  status: 200,
  message: 'Get studies successfully',
  code: 'MSG-0004',
} as StudyResponse;

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
        studyType: StudyType.Close,
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
        studyType: StudyType.Close,
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
        studyType: StudyType.Close,
      },
    ],
    countByStatus: {
      /* eslint-disable @typescript-eslint/naming-convention */
      A: 34,
      D: 0,
      E: 1,
      I: 12,
      N: 21,
      /* eslint-disable @typescript-eslint/naming-convention */
    },
  },

  status: 200,
  message: 'Get participant registry successfully',
  code: 'MSG-0013',
} as StudyDetails;

export const expectedStudyId = {id: '1'} as Study;

export const expectedResponse = {
  message: 'Target Enrollment updated successfully',
} as ApiResponse;

export const expectedTargetEnrollment: UpdateTargetEnrollmentRequest = {
  targetEnrollment: 12,
};
export const expectedSiteResponse = {
  message: 'New site added successfully',
} as ApiResponse;

export const expectedNewSite = {
  studyId: '1',
  locationId: '1',
} as AddSiteRequest;
