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
  expectedLocation,
  expectedLocations,
  expectedResponse,
  expectedLocatiodId,
  expectedLocationList,
} from 'src/app/entity/mockLocationData';
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

    entityServicespy.getCollection.and.returnValue(of(expectedLocationList));
    locationService
      .getLocations()
      .subscribe(
        (locations) =>
          expect(locations).toEqual(expectedLocationList, 'expected Locations'),
        fail,
      );

    expect(entityServicespy.getCollection.calls.count()).toBe(1, 'one call');
  });

  it('should return Locations list for the site creation', () => {
    const entityServicespy = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      ['getCollection'],
    );
    locationService = new LocationService(entityServicespy);

    entityServicespy.getCollection.and.returnValue(of(expectedLocation));
    locationService
      .getLocationsForSiteCreation(expectedLocatiodId.id.toString())
      .subscribe(
        (Locations) =>
          expect(Locations).toEqual(expectedLocation, 'expected Locations'),
        fail,
      );

    expect(entityServicespy.getCollection.calls.count()).toBe(1, 'one call');
  });

  it('should post the expected new Locations data', () => {
    const entityServicespyobj = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      ['post'],
    );
    locationService = new LocationService(entityServicespyobj);

    entityServicespyobj.post.and.returnValue(of(expectedResponse));

    locationService
      .addLocation(expectedLocations)
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

  it('should return an error when the server returns a error status code', fakeAsync(() => {
    const entityServicespy = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      ['getCollection'],
    );
    locationService = new LocationService(entityServicespy);
    const errorResponse: ApiResponse = {
      message: 'User does not exist',
    } as ApiResponse;

    entityServicespy.getCollection.and.returnValue(throwError(errorResponse));

    locationService.getLocations().subscribe(
      () => fail('expected an error, not locations'),
      (error: ApiResponse) => {
        expect(error.message).toContain('User does not exist');
      },
    );
  }));
});
