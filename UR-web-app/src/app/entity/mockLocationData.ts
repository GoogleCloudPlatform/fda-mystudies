import {Location} from 'src/app/site-coordinator/location/shared/location.model';

export const expectedLocation = {
  id: 2,
  customId: 'customid3',
  name: 'name -1-updated0',
  description: 'location-descp-updated',
  status: '0',
  studiesCount: 0,
} as Location;

export const expectedLocations = {
  id: 2,
  status: '1',
  customId: 'customIDlocation',
  name: 'Location Name',
  description: 'location Decription',
  studiesCount: 0,
} as Location;

export const updateList = {
  customId: 'customid123',
  name: 'name -123-updated0',
  description: 'location-desc-updated-now',
} as Location;

export const updatedLocation = {
  id: 2,
  status: '1',
  customId: 'customid123',
  name: 'Updated Location',
  description: 'Updated description',
  studiesCount: 0,
} as Location;

export const expectedLocationList = [
  {
    id: 2,
    customId: 'customid3',
    name: 'name -1-updated0',
    description: 'location-descp-updatedj',
    status: '1',
    studiesCount: 0,
  },
  {
    id: 3,
    customId: 'customid32',
    name: 'name -1 - updated000',
    description: 'location-descp-updated',
    status: '0',
    studiesCount: 0,
  },
] as Location[];
export const expectedResponse = {
  message: 'Location updated successfully',
} as Location;
export const updateLocation = {
  name: 'name -123-updated0',
  description: 'location-desc-updated-now',
  status: '1',
} as Location;
export const expectedLocatiodId = {id: 1} as Location;
export const expectedSiteStatus = {status: '1'} as Location;
