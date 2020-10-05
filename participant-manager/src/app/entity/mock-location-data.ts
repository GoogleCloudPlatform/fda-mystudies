import {
  Location,
  ManageLocations,
} from 'src/app/site-coordinator/location/shared/location.model';

export const expectedLocation = {
  locationId: '2',
  customId: 'customid3',
  name: 'name -1-updated0',
  description: 'location-descp-updated',
  studyNames: <string[]>[],
  status: 0,
} as Location;

export const expectedLocations = {
  locations: [
    {
      locationId: '2',
      status: 1,
      customId: 'customIDlocation',
      name: 'Location Name',
      description: 'location Description',
    },
  ],
} as ManageLocations;

export const updateList = {
  customId: 'customid123',
  name: 'name -123-updated0',
  description: 'location-desc-updated-now',
} as Location;

export const updatedLocation = {
  locationId: '2',
  status: 1,
  customId: 'customid123',
  name: 'Updated Location',
  description: 'Updated description',
} as Location;

export const expectedLocationList = {
  locations: [
    {
      locationId: '2',
      status: 1,
      customId: 'customid123',
      name: 'Updated Location',
      description: 'Updated description',
    },
    {
      locationId: '2',
      status: 1,
      customId: 'customid123',
      name: 'Updated Location',
      description: 'Updated description',
    },
  ],
} as ManageLocations;

export const expectedResponse = {
  message: 'Location updated successfully',
} as Location;

export const expectLocationDropdown = {
  locations: [{name: 'Location 1'}, {name: 'Location 2'}],
} as ManageLocations;

export const updateLocation = {
  name: 'name -123-updated0',
  description: 'location-desc-updated-now',
  status: 1,
} as Location;

export const expectedLocationId = {locationId: '1'} as Location;
export const expectedSiteStatus = {status: 1} as Location;
