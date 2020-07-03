import {TestBed, fakeAsync, tick} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {EntityService} from '../../../service/entity.service';
import {ApiResponse} from 'src/app/entity/error.model';
import {throwError, of} from 'rxjs';
import {DashboardModel} from '../shared/dashboard.model';
import {StudiesService} from './studies.service';

describe('StudiesService', () => {
  let studiesService: StudiesService;
  const expectedStudies = [
    {
      appId: 'dsdssd',
      appInfoId: 2,
      customId: 'dsasd',
      enrolledCount: 3,
      enrollmentPercentage: 25,
      id: 12,
      invitedCount: 2,
      name: 'dsadasd',
      sites: [],
      studyPermission: 2,
      totalSitesCount: 2,
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
      providers: [StudiesService, EntityService],
    });
  });

  it('should be created', () => {
    const service: StudiesService = TestBed.get(
      StudiesService,
    ) as StudiesService;
    expect(service).toBeTruthy();
  });

  it('should return expected Studies List', () => {
    const entityServicespy = jasmine.createSpyObj<
      EntityService<DashboardModel>
    >('EntityService', ['getCollection']);
    studiesService = new StudiesService(entityServicespy);

    entityServicespy.getCollection.and.returnValue(of(expectedStudies));
    studiesService
      .getStudies()
      .subscribe(
        (Studies) =>
          expect(Studies).toEqual(expectedStudies, 'expected StudiesList'),
        fail,
      );

    expect(entityServicespy.getCollection.calls.count()).toBe(1, 'one call');
  });
  it('should return an error when the server returns a 401', fakeAsync(() => {
    const entityServicespy = jasmine.createSpyObj<
      EntityService<DashboardModel>
    >('EntityService', ['getCollection']);
    studiesService = new StudiesService(entityServicespy);
    const errorResponse: ApiResponse = {
      error: {
        userMessage: 'User does not exist',
        type: 'error',
        detailMessage: '404 Cant able to get the details',
      },
    };

    entityServicespy.getCollection.and.returnValue(throwError(errorResponse));

    studiesService.getStudies().subscribe(
      () => fail('expected an error'),
      (error: ApiResponse) => {
        expect(error.error.userMessage).toContain('User does not exist');
      },
    );
  }));

  it('should return an error when the server returns a 400', fakeAsync(() => {
    const entityServicespy = jasmine.createSpyObj<
      EntityService<DashboardModel>
    >('EntityService', ['getCollection']);
    studiesService = new StudiesService(entityServicespy);
    const errorResponses: ApiResponse = {
      error: {
        userMessage: 'Bad Request',
        type: 'error',
        detailMessage:
          'Missing request header userId for method parameter of type Integer',
      },
    };

    entityServicespy.getCollection.and.returnValue(throwError(errorResponses));
    tick(40);
    studiesService.getStudies().subscribe(
      () => fail('expected an error'),
      (error: ApiResponse) => {
        expect(error.error.userMessage).toBe('Bad Request');
      },
    );
  }));
});
