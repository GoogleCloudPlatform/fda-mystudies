import {TestBed, fakeAsync} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {LocationService} from './location.service';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {EntityService} from '../../../service/entity.service';
import {throwError, of} from 'rxjs';
import {Location} from '../shared/location.model';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {
  expectedLocations,
  expectedLocation,
  expectedLocationDetails,
  expectedResponse,
  expectedLocatiodId,
} from 'src/app/entity/mockData';
describe('LocationService', () => {
  let locationService: LocationService;

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
      ['getCollection'],
    );
    locationService = new LocationService(entityServicespy);

    entityServicespy.getCollection.and.returnValue(of(expectedLocations));
    locationService
      .getLocations()
      .subscribe(
        (Locations) =>
          expect(Locations).toEqual(expectedLocations, 'expected Locations'),
        fail,
      );

    expect(entityServicespy.getCollection.calls.count()).toBe(1, 'one call');
  });

  it('should return expected Locations details of specific id', () => {
    const entityServiceSpy = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      ['getCollection'],
    );
    locationService = new LocationService(entityServiceSpy);
    entityServiceSpy.getCollection.and.returnValue(of(expectedLocationDetails));
    locationService
      .getLocationDetails(expectedLocatiodId)
      .subscribe(
        (Locations) =>
          expect(Locations).toEqual(
            expectedLocationDetails,
            'expected Locations details based on id',
          ),
        fail,
      );

    expect(entityServiceSpy.getCollection).toHaveBeenCalledTimes(1);
  });

  it('should post the expected new Locations data', () => {
    const entityServicespyobj = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      ['post'],
    );
    locationService = new LocationService(entityServicespyobj);

    entityServicespyobj.post.and.returnValue(of(expectedResponse));

    locationService
      .addLocation(expectedLocation)
      .subscribe(
        (succesResponse: Location) =>
          expect(succesResponse).toEqual(
            expectedResponse,
            '{code:200,message:New location added successfully}',
          ),
        fail,
      );

    expect(entityServicespyobj.post.calls.count()).toBe(1, 'one call');
  });

  it('should put the expected updated Locations data', () => {
    const entityServicespyobj = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      ['put'],
    );

    locationService = new LocationService(entityServicespyobj);

    entityServicespyobj.put.and.returnValue(of(expectedResponse));

    locationService
      .updateLocation(expectedLocation, expectedLocatiodId)
      .subscribe(
        (succesResponse: Location) =>
          expect(succesResponse).toEqual(
            expectedResponse,
            '{code:200,message:Location updated successfully}',
          ),
        fail,
      );

    expect(entityServicespyobj.put.calls.count()).toBe(1, 'one call');
  });

  it('should return an error when the server returns a error status code', fakeAsync(() => {
    const entityServicespy = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      ['getCollection'],
    );
    locationService = new LocationService(entityServicespy);
    const errorResponse: ApiResponse = {
      message: 'User does not exist',
      code: 'MSG_013',
    };

    entityServicespy.getCollection.and.returnValue(throwError(errorResponse));

    locationService.getLocations().subscribe(
      () => fail('expected an error, not locations'),
      (error: ApiResponse) => {
        expect(error.message).toContain('User does not exist');
      },
    );
  }));
});
