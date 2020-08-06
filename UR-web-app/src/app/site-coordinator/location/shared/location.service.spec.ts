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
import * as expectedResult from 'src/app/entity/mockLocationData';

describe('LocationService', () => {
  let locationService: LocationService;
  let httpServicespyobj: jasmine.SpyObj<HttpClient>;

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
    const entityServicespy = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      {getCollection: of(expectedResult.expectedLocationList)},
    );
    locationService = new LocationService(entityServicespy, httpServicespyobj);

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

    expect(entityServicespy.getCollection).toHaveBeenCalledTimes(1);
  });

  it('should return Locations list for the site creation', () => {
    const entityServicespy = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      {getCollection: of(expectedResult.expectedLocations)},
    );
    locationService = new LocationService(entityServicespy, httpServicespyobj);

    locationService
      .getLocationsForSiteCreation(
        expectedResult.expectedLocatiodId.id.toString(),
      )
      .subscribe(
        (Locations) =>
          expect(Locations).toEqual(
            expectedResult.expectedLocations,
            'expected Locations',
          ),
        fail,
      );
    expect(entityServicespy.getCollection).toHaveBeenCalledTimes(1);
  });

  it('should return expected Locations details of specific id', () => {
    const entityServiceSpy = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      {get: of(expectedResult.expectedLocation)},
    );
    locationService = new LocationService(entityServiceSpy, httpServicespyobj);
    locationService
      .get(expectedResult.expectedLocatiodId.id.toString())
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
      httpServicespyobj,
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
    const httpServicespyobj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      put: of(expectedResult.expectedResponse),
    });
    locationService = new LocationService(
      entityServicespyobj,
      httpServicespyobj,
    );
    locationService
      .update(
        expectedResult.expectedLocation,
        expectedResult.expectedLocatiodId.id.toString(),
      )
      .subscribe(
        (succesResponse: UpdateLocationResponse) =>
          expect(succesResponse).toEqual(
            expectedResult.expectedResponse,
            '{code:MSG_013,message:Location updated successfully}',
          ),
        fail,
      );
    expect(httpServicespyobj.put).toHaveBeenCalledTimes(1);
  });

  it('should return an error when the server returns a error status code', fakeAsync(() => {
    const errorResponse: ApiResponse = {
      message: 'User does not exist',
    } as ApiResponse;

    const entityServicespy = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      {getCollection: throwError(errorResponse)},
    );
    locationService = new LocationService(entityServicespy, httpServicespyobj);

    locationService.getLocations().subscribe(
      () => fail('expected an error, not locations'),
      (error: ApiResponse) => {
        expect(error.message).toContain('User does not exist');
      },
    );
  }));
});
