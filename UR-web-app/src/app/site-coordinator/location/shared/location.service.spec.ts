import {TestBed, fakeAsync, tick} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {LocationService} from './location.service';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {EntityService} from '../../../service/entity.service';
import {ApiResponse} from 'src/app/entity/error.model';
import {HttpErrorResponse} from '@angular/common/http';
import {throwError, of} from 'rxjs';
import {Location} from '../shared/location.model';

describe('LocationService', () => {
  let locationService: LocationService;
  const expectedLocations = [
    {
      id: 2,
      customId: 'customid3',
      name: 'name -1-updated0',
      description: 'location-descp-updatedj',
      status: '1',
      studiesCount: 0,
      studies: [],
    },
    {
      id: 3,
      customId: 'customid32',
      name: 'name -1 - updated000',
      description: 'location-descp-updated',
      status: '0',
      studiesCount: 0,
      studies: [],
    },
  ];
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

  it('should return an error when the server returns a 401', fakeAsync(() => {
    const entityServicespy = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      ['getCollection'],
    );
    locationService = new LocationService(entityServicespy);
    const errorResponse = new HttpErrorResponse({
      error: {
        app_error_code: 401,
        userMessage: 'User does not exist',
        type: 'error',
        detailMessage: '404 Cant able to get details',
      },
      status: 401,
      statusText: 'Unauthorized',
    });

    entityServicespy.getCollection.and.returnValue(throwError(errorResponse));

    locationService.getLocations().subscribe(
      () => fail('expected an error, not locations'),
      (error: ApiResponse) => {
        expect(error.error.userMessage).toContain('User does not exist');
      },
    );
  }));

  it('should return an error when the server returns a 400', fakeAsync(() => {
    const entityServicespy = jasmine.createSpyObj<EntityService<Location>>(
      'EntityService',
      ['getCollection'],
    );
    locationService = new LocationService(entityServicespy);

    const errorResponses = new HttpErrorResponse({
      error: {
        app_error_code: 400,
        userMessage: 'Bad Request',
        type: 'error',
        detailMessage:
          'Missing request header userId for method parameter of type Integer',
      },
      status: 401,
      statusText: 'Unauthorized',
    });

    entityServicespy.getCollection.and.returnValue(throwError(errorResponses));
    tick(40);
    locationService.getLocations().subscribe(
      () => fail('expected an error, not locations'),
      (error: ApiResponse) => {
        expect(error.error.userMessage).toBe('Bad Request');
      },
    );
  }));
});
