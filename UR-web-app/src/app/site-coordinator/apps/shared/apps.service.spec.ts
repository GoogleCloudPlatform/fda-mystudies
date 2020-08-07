import {TestBed, fakeAsync, tick} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {EntityService} from '../../../service/entity.service';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {throwError, of} from 'rxjs';
import {App} from './app.model';
import {expectedAppList} from 'src/app/entity/mock-apps-data';
import {AppsService} from './apps.service';

describe('StudiesService', () => {
  let appsService: AppsService;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SiteCoordinatorModule,
        RouterTestingModule.withRoutes([]),
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [AppsService, EntityService],
    });
  });

  it('should be created', () => {
    const service: AppsService = TestBed.get(AppsService) as AppsService;
    expect(service).toBeTruthy();
  });

  it('should return expected Apps List', fakeAsync(() => {
    const entityServicespy = jasmine.createSpyObj<EntityService<App>>(
      'EntityService',
      {getCollection: of(expectedAppList)},
    );
    appsService = new AppsService(entityServicespy);

    appsService
      .getApps()
      .subscribe(
        (app) => expect(app).toEqual(expectedAppList, 'expected AppsList'),
        fail,
      );
    expect(entityServicespy.getCollection).toHaveBeenCalledTimes(1);
  }));

  it('should return an error when the server returns a 400', fakeAsync(() => {
    const errorResponses: ApiResponse = {
      message: 'Bad Request',
    } as ApiResponse;
    const entityServicespy = jasmine.createSpyObj<EntityService<App>>(
      'EntityService',
      {getCollection: throwError(errorResponses)},
    );
    appsService = new AppsService(entityServicespy);

    tick(40);
    appsService.getApps().subscribe(
      () => fail('expected an error'),
      (error: ApiResponse) => {
        expect(error.message).toBe('Bad Request');
      },
    );
  }));
});
