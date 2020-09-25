import {TestBed, fakeAsync, tick} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {EntityService} from '../../../service/entity.service';
import {ApiResponse} from '../../../entity/api.response.model';
import {throwError, of} from 'rxjs';
import {
  expectedAppDetails,
  expectedAppId,
} from '../../../entity/mock-apps-data';
import {AppDetailsService} from './app-details.service';
import {AppDetails} from './app-details';

describe('AppDetailsService', () => {
  let appDetailsService: AppDetailsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SiteCoordinatorModule,
        RouterTestingModule.withRoutes([]),
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [AppDetailsService, EntityService],
    });
  });

  it('should be created', () => {
    const service: AppDetailsService = TestBed.get(
      AppDetailsService,
    ) as AppDetailsService;
    expect(service).toBeTruthy();
  });

  it('should return expected App details', fakeAsync(() => {
    const entityServiceSpy = jasmine.createSpyObj<EntityService<AppDetails>>(
      'EntityService',
      {get: of(expectedAppDetails)},
    );
    appDetailsService = new AppDetailsService(entityServiceSpy);

    appDetailsService
      .get(expectedAppId.appId)
      .subscribe(
        (appDetail) =>
          expect(appDetail).toEqual(
            expectedAppDetails,
            'expected App Details List',
          ),
        fail,
      );
    expect(entityServiceSpy.get).toHaveBeenCalledTimes(1);
  }));

  it('should return an error when the server returns a 400', fakeAsync(() => {
    const errorResponses: ApiResponse = {
      message: 'Bad Request',
    } as ApiResponse;
    const entityServicespy = jasmine.createSpyObj<EntityService<AppDetails>>(
      'EntityService',
      {get: throwError(errorResponses)},
    );
    appDetailsService = new AppDetailsService(entityServicespy);

    tick(40);
    appDetailsService.get(expectedAppId.appId).subscribe(
      () => fail('expected an error'),
      (error: ApiResponse) => {
        expect(error.message).toBe('Bad Request');
      },
    );
  }));
});
