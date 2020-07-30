import {Location} from 'src/app/site-coordinator/location/shared/location.model';
export const expectedLocation: Location[] = [
  {
    id: 2,
    customId: 'customid3',
    name: 'name -1-updated0',
    description: 'location-descp-updated',
    status: '1',
    studiesCount: 0,
  },
] as Location[];
export const expectedLocations: Location = {
  id: 2,
  status: '0',
  customId: 'customIDlocation',
  name: 'Location Name',
  description: 'location Decription',
  studiesCount: 0,
} as Location;
export const updateList: Location[] = [
  {
    customId: 'customid123',
    name: 'name -123-updated0',
    description: 'location-desc-updated-now',
  },
] as Location[];

export const expectedLocationList: Location[] = [
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

export const expectedResponse: Location = {
  message: 'Location updated successfully',
} as Location;

export const expectLocationDropdown: Location[] = [
  {name: 'Location 1'},
  {name: 'Location 2'},
] as Location[];

export const updateLocation: Location = {
  name: 'name -123-updated0',
  description: 'location-desc-updated-now',
  status: '1',
} as Location;

export const expectedLocatiodId: Location = {id: 1} as Location;
