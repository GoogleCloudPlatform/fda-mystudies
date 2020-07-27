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
export const expectedStudyId = 1;
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
];
export const expectedSiteResponse = {
  studyId: '1',
  locationId: '1',
  message: 'New site added successfully',
  code: '200',
};
export const expectedNewSite = {
  studyId: '1',
  locationId: '1',
  message: '',
  code: '',
};
