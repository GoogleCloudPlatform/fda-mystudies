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
import {expectedStudyList} from 'src/app/entity/mock-studies-data';

describe('StudiesService', () => {
  let studiesService: StudiesService;

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

    entityServicespy.getCollection.and.returnValue(of(expectedStudyList));
    studiesService
      .getStudies()
      .subscribe(
        (studies) =>
          expect(studies).toEqual(expectedStudyList, 'expected StudiesList'),
        fail,
      );

    expect(entityServicespy.getCollection.calls.count()).toBe(1, 'one call');
  }));

  it('should return expected Sites List', fakeAsync(() => {
    const entityServicespy = jasmine.createSpyObj<EntityService<Study>>(
      'EntityService',
      ['getCollection'],
    );
    studiesService = new StudiesService(entityServicespy);

    entityServicespy.getCollection.and.returnValue(of(expectedStudyList));
    studiesService
      .getStudiesWithSites()
      .subscribe(
        (studies) =>
          expect(studies).toEqual(expectedStudyList, 'expected StudiesList'),
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
    } as ApiResponse;

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
