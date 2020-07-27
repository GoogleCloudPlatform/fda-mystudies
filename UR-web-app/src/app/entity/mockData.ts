import {of, Observable} from 'rxjs';
import {Study} from '../site-coordinator/studies/shared/study.model';

export const expectedList = [
  {
    id: 2,
    customId: 'customid3',
    name: 'name -1-updated0',
    description: 'location-descp-updated',
    status: '1',
    studiesCount: 0,
    message: '',
    code: '',
  },
];
export const updateList = [
  {
    customId: 'customid123',
    name: 'name -123-updated0',
    description: 'location-desc-updated-now',
  },
];

export const expectedLocations = [
  {
    id: 2,
    customId: 'customid3',
    name: 'name -1-updated0',
    description: 'location-descp-updatedj',
    status: '1',
    studiesCount: 0,
    message: '',
    code: '',
  },
  {
    id: 3,
    customId: 'customid32',
    name: 'name -1 - updated000',
    description: 'location-descp-updated',
    status: '0',
    studiesCount: 0,
    message: '',
    code: '',
  },
];
export const expectedLocation = {
  id: 0,
  status: '0',
  customId: 'customIDlocation',
  name: 'Location Name',
  description: 'location Decription',
  studiesCount: 0,
  message: '',
  code: '',
};
export const expectedLocationDetails = [
  {
    id: 2,
    status: '1',
    customId: 'customIDlocation',
    name: 'Location Name',
    description: 'location Decription',
    studiesCount: 0,
    message: '',
    code: '',
  },
];
export const expectedResponse = {
  id: 0,
  status: '0',
  customId: '',
  name: '',
  description: '',
  studiesCount: 0,
  message: 'Location updated successfully',
  code: '200',
};
export const updateLocation = [
  {
    name: 'name -123-updated0',
    description: 'location-desc-updated-now',
    status: '1',
  },
];
export const expectedLocatiodId = '1';

export const expectedStudyList: Observable<Study[]> = of([
  {
    appId: '',
    appInfoId: 0,
    customId: 'NewStudyTest',
    enrolledCount: 41,
    enrollmentPercentage: 38,
    id: 1,
    invitedCount: 0,
    name: 'New Study Test',
    sites: [],
    studyPermission: 0,
    totalSitesCount: 16,
    type: 'OPEN',
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
    sites: [],
    studyPermission: 1,
    totalSitesCount: 5,
    type: 'OPEN',
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
    type: 'CLOSE',
    logo: '/path_to_img/',
  },
]);
