import {App} from '../site-coordinator/apps/shared/app.model';
import {AppDetails} from '../site-coordinator/apps/shared/app-details';

export const expectedAppList = [
  {
    id: 2,
    customId: 'GCPMS002',
    name: 'App Name_GCPMS002',
    invitedCount: 11,
    enrolledCount: 1,
    enrollmentPercentage: 9.0909090909090922,
    totalStudiesCount: 1,
    appUsersCount: 0,
    appPermission: 2,
  },
  {
    id: 7,
    customId: 'CCFSBP002',
    name: 'App Name_CCFSBP002',
    invitedCount: 0,
    enrolledCount: 0,
    enrollmentPercentage: 0,
    totalStudiesCount: 1,
    appUsersCount: 0,
    appPermission: 2,
  },
] as App[];

export const expectedAppDetails = {
  appId: '1',
  customId: 'MyStudies-Id-1',
  name: 'MyStudies-1',
  participants: [
    {
      email: 'mockit_email@grr.la',
      userDetailsId: '402861ASDd0173bdef1e590007',
      registrationStatus: 'Active',
      registrationDate: '08/01/2020',
      enrolledStudies: [
        {
          studyId: '4028617973bdef0d0173bdef1e4a0004',
          customStudyId: 'CovidStudy',
          studyName: 'COVID Test Study',
          sites: [
            {
              siteId: '4028617973bdef0d0173bdef1e530005',
              customSiteId: 'OpenStudy02',
              siteName: 'Marlborough',
              enrollmentDate: '10/05/2020',
              withdrawlDate: '08/05/2020',
              siteStatus: 'Active',
            },
          ],
        },
        {
          studyId: '43dfdfbbdef1e4a0004',
          customStudyId: 'Covid Study 1',
          studyName: 'COVID New Study',
          sites: [
            {
              siteId: '5602861fdgfg30005',
              customSiteId: 'TestOpenStudy',
              siteName: 'Americano',
              enrollmentDate: '11/03/2020',
              withdrawlDate: '28/06/2020',
              siteStatus: 'Active',
            },
          ],
        },
      ],
      enrollments: [],
      consentHistory: [],
    },
  ],
  status: 200,
  message: 'get App Participants successfully',
  code: 'MSG-0021',
} as AppDetails;

export const expectedAppId = {appId: '1'} as AppDetails;
