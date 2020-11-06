import {TestBed, fakeAsync} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {LocationService} from './location.service';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {EntityService} from '../../../service/entity.service';
import {throwError, of} from 'rxjs';
import {Location, UpdateLocationResponse} from '../shared/location.model';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {HttpClient} from '@angular/common/http';
import * as expectedResult from 'src/app/entity/mock-location-data';

describe('LocationService', () => {
  let locationService: LocationService;
  let httpServiceSpyObj: jasmine.SpyObj<HttpClient>;
  let entityServiceSpy: jasmine.SpyObj<EntityService<Location>>;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SiteCoordinatorModule,
        RouterTestingModule.withRoutes([]),
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [LocationService, EntityService, BsModalService, BsModalRef],
    });
  });

  it('should be created', () => {
    const service: LocationService = TestBed.get(
      LocationService,
    ) as LocationService;
    expect(service).toBeTruthy();
  });

  it('should return expected Locations List', () => {
    const httpServicespyobj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      get: of(expectedResult.expectedLocationList),
    });
    locationService = new LocationService(entityServiceSpy, httpServicespyobj);

    locationService
      .getLocations()
      .subscribe(
        (locations) =>
          expect(locations).toEqual(
            expectedResult.expectedLocationList,
            'expected Locations',
          ),
        fail,
      );

    expect(httpServicespyobj.get).toHaveBeenCalledTimes(1);
  });

  it('should return Locations list for the site creation', () => {
    const httpServicespyobj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      get: of(expectedResult.expectedLocations),
    });
    locationService = new LocationService(entityServiceSpy, httpServicespyobj);

    locationService
      .getLocationsForSiteCreation(expectedResult.expectedLocationId.locationId)
      .subscribe(
        (locations) =>
          expect(locations).toEqual(
            expectedResult.expectedLocations,
            'expected Locations',
          ),
        fail,
      );
    expect(httpServicespyobj.get).toHaveBeenCalledTimes(1);
  });

  it('should return expected Locations details of specific id', () => {
    const entityServiceSpy = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      {get: of(expectedResult.expectedLocation)},
    );
    locationService = new LocationService(entityServiceSpy, httpServiceSpyObj);
    locationService
      .get(expectedResult.expectedLocationId.locationId)
      .subscribe(
        (locations) =>
          expect(locations).toEqual(
            expectedResult.expectedLocation,
            'expected Locations details based on id',
          ),
        fail,
      );

    expect(entityServiceSpy.get).toHaveBeenCalledTimes(1);
  });

  it('should post the expected new Locations data', () => {
    const entityServicespyobj = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      {post: of(expectedResult.expectedResponse)},
    );
    locationService = new LocationService(
      entityServicespyobj,
      httpServiceSpyObj,
    );

    locationService
      .addLocation(expectedResult.expectedLocation)
      .subscribe(
        (succesResponse: Location) =>
          expect(succesResponse).toEqual(
            expectedResult.expectedResponse,
            '{code:200,message:New location added successfully}',
          ),
        fail,
      );
    expect(entityServicespyobj.post).toHaveBeenCalledTimes(1);
  });

  it('should put the expected updated Locations data', () => {
    const entityServicespyobj = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      {put: of(expectedResult.expectedResponse)},
    );
    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      put: of(expectedResult.expectedResponse),
    });
    locationService = new LocationService(
      entityServicespyobj,
      httpServiceSpyObj,
    );
    locationService
      .update(
        expectedResult.expectedLocation,
        expectedResult.expectedLocationId.locationId,
      )
      .subscribe(
        (succesResponse: UpdateLocationResponse) =>
          expect(succesResponse).toEqual(
            expectedResult.expectedResponse,
            '{code:MSG_013,message:Location updated successfully}',
          ),
        fail,
      );
    expect(httpServiceSpyObj.put).toHaveBeenCalledTimes(1);
  });

  it('should return an error when the server returns a error status code', fakeAsync(() => {
    const errorResponse = {
      message: 'User does not exist',
    } as ApiResponse;
    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      get: throwError(errorResponse),
    });
    locationService = new LocationService(entityServiceSpy, httpServiceSpyObj);

    locationService.getLocations().subscribe(
      () => fail('expected an error, not locations'),
      (error: ApiResponse) => {
        expect(error.message).toContain('User does not exist');
      },
    );
  }));
});
