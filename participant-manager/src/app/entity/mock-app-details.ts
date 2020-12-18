import {AppDetails} from '../site-coordinator/user/shared/app-details';
import {ApiResponse} from './api.response.model';
import {User} from './user';

export const addUserResponse = {
  message: 'User added sucessfully',
} as ApiResponse;

export const addUserRequest = {
  id: '1',
  firstName: 'super',
  lastName: 'admin',
  superAdmin: true,
  email: 'super@grr.la',
  manageLocations: null,
} as User;

export const expectedAppDetails = {
  apps: [
    {
      id: '1',
      customId: 'App001',
      name: 'AppName1',
      selected: false,
      permission: null,
      totalSitesCount: 2,
      selectedSitesCount: 0,
      studies: [
        {
          studyId: '20',
          customStudyId: 'Study001',
          studyName: 'StudyName1',
          selected: false,
          permission: null,
          totalSitesCount: 2,
          selectedSitesCount: 0,
          sites: [
            {
              siteId: '30',
              locationId: '40',
              customLocationId: 'Location1',
              locationName: 'LocationName1',
              selected: false,
              permission: null,
              locationDescription: 'LocationDesc1',
            },
            {
              siteId: '43',
              locationId: '40',
              customLocationId: 'Location1',
              locationName: 'LocationName1',
              selected: false,
              permission: null,
              locationDescription: 'LocationDesc1',
            },
          ],
        },
      ],
    },
    {
      id: '2',
      customId: 'App002',
      name: 'AppName2',
      selected: false,
      permission: null,
      selectedSitesCount: 0,
      totalSitesCount: 2,
      studies: [
        {
          studyId: '21',
          customStudyId: 'Study002',
          studyName: 'SyudyName2',
          selected: false,
          permission: null,
          totalSitesCount: 1,
          selectedSitesCount: 0,
          sites: [
            {
              siteId: '42',
              locationId: '41',
              customLocationId: 'Location2',
              locationName: 'LocationName2',
              selected: false,
              permission: null,
              locationDescription: 'LocationDesc2',
            },
          ],
        },
        {
          studyId: '22',
          customStudyId: 'Study003',
          studyName: 'StudyName3',
          selected: false,
          permission: null,
          totalSitesCount: 1,
          selectedSitesCount: 0,
          sites: [
            {
              siteId: '44',
              locationId: '41',
              customLocationId: 'Location2',
              locationName: 'LocationName2',
              selected: false,
              permission: null,
              locationDescription: 'LocationDesc2',
            },
          ],
        },
      ],
    },
  ],
  studyPermissionCount: 0,
  status: 200,
  message: 'Get App Details successfully',
  code: 'MSG-0019',
} as AppDetails;
