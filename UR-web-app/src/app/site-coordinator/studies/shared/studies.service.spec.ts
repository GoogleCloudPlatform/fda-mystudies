import {TestBed, fakeAsync, tick} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {EntityService} from '../../../service/entity.service';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {throwError, of} from 'rxjs';
import {Study} from './study.model';
import {StudiesService} from './studies.service';

describe('StudiesService', () => {
  let studiesService: StudiesService;
  const expectedStudies = [
    {
      appId: '',
      appInfoId: 0,
      customId: 'NewStudyTest',
      enrolledCount: 41,
      enrollmentPercentage: 38,
      id: 1,
      invitedCount: 0,
      name: 'New Study Test',
      sites: [],
      studyPermission: 0,
      totalSitesCount: 16,
      type: 'OPEN',
      logo: '/path_to_img/',
    },
    {
      appId: '',
      appInfoId: 0,
      customId: 'OpenStudy',
      enrolledCount: 5,
      enrollmentPercentage: 0,
      id: 12,
      invitedCount: 9,
      name: 'Open Study 02',
      sites: [],
      studyPermission: 1,
      totalSitesCount: 5,
      type: 'OPEN',
      logo: '/path_to_img/',
    },
    {
      appId: '',
      appInfoId: 0,
      customId: 'ClosedStudy',
      enrolledCount: 54,
      enrollmentPercentage: 17,
      id: 14,
      invitedCount: 0,
      name: 'Closed Study',
      sites: [],
      studyPermission: 2,
      totalSitesCount: 6,
      type: 'CLOSE',
      logo: '/path_to_img/',
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

  it('should return expected Studies List', fakeAsync(() => {
    const entityServicespy = jasmine.createSpyObj<EntityService<Study>>(
      'EntityService',
      ['getCollection'],
    );
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
  }));

  it('should return an error when the server returns a 400', fakeAsync(() => {
    const entityServicespy = jasmine.createSpyObj<EntityService<Study>>(
      'EntityService',
      ['getCollection'],
    );
    studiesService = new StudiesService(entityServicespy);
    const errorResponses: ApiResponse = {
      message: 'Bad Request',
      code: 'ER_005',
    };

    entityServicespy.getCollection.and.returnValue(throwError(errorResponses));
    tick(40);
    studiesService.getStudies().subscribe(
      () => fail('expected an error'),
      (error: ApiResponse) => {
        expect(error.message).toBe('Bad Request');
      },
    );
  }));
});
