import {ManageApps} from '../site-coordinator/apps/shared/app.model';
import {AppDetails} from '../site-coordinator/apps/shared/app-details';
import {StudyType} from '../shared/enums';

export const expectedAppList = {
  apps: [
    {
      id: '2',
      customId: 'GCPMS002',
      name: 'App Name_GCPMS002',
      invitedCount: 11,
      enrolledCount: 1,
      enrollmentPercentage: 9.0909090909090922,
      studiesCount: 1,
      appUsersCount: 0,
      permission: 2,
    },
    {
      id: '7',
      customId: 'CCFSBP002',
      name: 'App Name_CCFSBP002',
      invitedCount: 0,
      enrolledCount: 0,
      enrollmentPercentage: 0,
      studiesCount: 1,
      appUsersCount: 0,
      permission: 2,
    },
  ],
} as ManageApps;

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
          studyType: StudyType.Close,
          sites: [
            {
              siteId: '4028617973bdef0d0173bdef1e530005',
              customSiteId: 'OpenStudy02',
              siteName: 'Marlborough',
              enrollmentDate: '10/05/2020',
              withdrawlDate: '08/05/2020',
              participantStudyStatus: 'Active',
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
              participantStudyStatus: 'Active',
            },
          ],
        },
      ],
      enrollments: [],
      consentHistory: [],
    },
    {
      email: 'mockitTest@grr.la',
      userDetailsId: '2861A007',
      registrationStatus: 'Active',
      registrationDate: '8/11/2019',
      enrolledStudies: [
        {
          studyId: '4ef1e4a0004',
          customStudyId: 'NewCovidStudy2019',
          studyName: 'TestStudy',
          studyType: StudyType.Close,

          sites: [
            {
              siteId: '617973b45',
              customLocationId: 'EcternalStudy12',
              locationName: 'Americano',
              enrollmentDate: '11/05/2020',
              withdrawlDate: '02/08/2020',
              participantStudyStatus: 'Active',
            },
          ],
        },
        {
          studyId: '3dfde4a00',
          customStudyId: 'latestStudy',
          studyName: 'Latest Updated Study',
          sites: [
            {
              siteId: '61fdgfg307',
              customSiteId: 'Local Testdata',
              siteName: 'Espacino',
              enrollmentDate: '29/03/2020',
              withdrawlDate: '16/06/2020',
              participantStudyStatus: 'Active',
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

export const filteredEmail = {
  appId: '1',
  customId: 'MyStudies-Id-1',
  name: 'MyStudies-1',
  participants: [
    {
      email: 'mockitTest@grr.la',
      userDetailsId: '2861A007',
      registrationStatus: 'Active',
      registrationDate: '8/11/2019',
      enrolledStudies: [
        {
          studyId: '4ef1e4a0004',
          customStudyId: 'NewCovidStudy2019',
          studyName: 'TestStudy',
          studyType: StudyType.Close,
          sites: [
            {
              siteId: '617973b45',
              customLocationId: 'EcternalStudy12',
              locationName: 'Americano',
              enrollmentDate: '11/05/2020',
              withdrawlDate: '02/08/2020',
              participantStudyStatus: 'Active',
            },
          ],
        },
        {
          studyId: '3dfde4a00',
          customStudyId: 'latestStudy',
          studyName: 'Latest Updated Study',
          sites: [
            {
              siteId: '61fdgfg307',
              customSiteId: 'Local Testdata',
              siteName: 'Espacino',
              enrollmentDate: '29/03/2020',
              withdrawlDate: '16/06/2020',
              participantStudyStatus: 'Active',
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
