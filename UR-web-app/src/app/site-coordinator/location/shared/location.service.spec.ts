/* eslint-disable @typescript-eslint/no-unsafe-assignment */
/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-unsafe-call */

import {TestBed} from '@angular/core/testing';
import {
  HttpTestingController,
  HttpClientTestingModule,
} from '@angular/common/http/testing';
import {LocationService} from './location.service';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {EntityService} from '../../../service/entity.service';
import {asyncError, asyncData} from 'src/testing/async-observable-helpers';
import {ApiResponse} from 'src/app/entity/error.model';
import {HttpErrorResponse} from '@angular/common/http';
describe('LocationService', () => {
  let locationService: LocationService;
  let httpMock: HttpTestingController;
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
    httpMock = TestBed.get(HttpTestingController) as HttpTestingController;
  });
  beforeEach(() => {
    const entityServicespy = jasmine.createSpyObj('EntityService', [
      'getCollection',
    ]);
    locationService = new LocationService(entityServicespy);
  });

  it('should be created', () => {
    const service: LocationService = TestBed.get(
      LocationService,
    ) as LocationService;
    expect(service).toBeTruthy();
  });

  it('should return expected Locations List', () => {
    const entityServicespy = jasmine.createSpyObj('EntityService', [
      'getCollection',
    ]);
    locationService = new LocationService(entityServicespy);

    entityServicespy.getCollection.and.returnValue(
      asyncData(expectedLocations),
    );
    locationService
      .getLocations()
      .subscribe(
        (Locations) =>
          expect(Locations).toEqual(expectedLocations, 'expected Locations'),
        fail,
      );

    expect(entityServicespy.getCollection.calls.count()).toBe(1, 'one call');
  });

  it('should return an error when the server returns a 401', () => {
    const entityServicespy = jasmine.createSpyObj('EntityService', [
      'getCollection',
    ]);
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

    entityServicespy.getCollection.and.returnValue(asyncError(errorResponse));

    locationService.getLocations().subscribe(
      () => fail('expected an error, not locations'),
      (error: ApiResponse) => {
        expect(error.error.userMessage).toContain('User does not exist');
      },
    );
  });

  it('should return an error when the server returns a 400', () => {
    const entityServicespy = jasmine.createSpyObj('EntityService', [
      'getCollection',
    ]);
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

    entityServicespy.getCollection.and.returnValue(asyncError(errorResponses));

    locationService.getLocations().subscribe(
      () => fail('expected an error, not locations'),
      (error: ApiResponse) => {
        expect(error.error.userMessage).toContain('Bad Request');
      },
    );
  });

  afterEach(() => {
    httpMock.verify();
  });
});
