import {Study} from '../site-coordinator/studies/shared/study.model';
import {AddSite} from '../site-coordinator/sites/shared/add.sites.model';

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

export const expectedSiteResponse = {
  studyId: '1',
  locationId: '1',
  message: 'New site added successfully',
} as AddSite;

export const expectedNewSite = {
  studyId: '1',
  locationId: '1',
} as AddSite;

export const expectedStudyId = {id: 1} as Study;
