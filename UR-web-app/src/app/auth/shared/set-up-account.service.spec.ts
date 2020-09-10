import {TestBed, fakeAsync, tick} from '@angular/core/testing';
import {SetUpAccountService} from './set-up-account.service';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import {SiteCoordinatorModule} from 'src/app/site-coordinator/site-coordinator.module';
import {RouterTestingModule} from '@angular/router/testing';
import {EntityService} from 'src/app/service/entity.service';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {User} from 'src/app/entity/user';
import {HttpClient} from '@angular/common/http';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {throwError, of} from 'rxjs';
import {
  expectedUserDetails,
  expectedSetUpCode,
  expectedsetUpResponse,
  expectedUpdateSetUp,
} from 'src/app/entity/mock-user-data';
import {environment} from '@environment';

describe('SetUpAccountService', () => {
  let setUpAccountServices: SetUpAccountService;
  let httpServiceSpyObj: jasmine.SpyObj<HttpClient>;

  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SiteCoordinatorModule,
        RouterTestingModule.withRoutes([]),
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [SetUpAccountService, EntityService],
    }),
  );

  it('should be created', () => {
    const service: SetUpAccountService = TestBed.get(
      SetUpAccountService,
    ) as SetUpAccountService;
    expect(service).toBeTruthy();
  });

  it('should return expected User details to setup account', fakeAsync(() => {
    const entityServiceSpy = jasmine.createSpyObj<EntityService<User>>(
      'EntityService',
      {get: of(expectedUserDetails)},
    );
    setUpAccountServices = new SetUpAccountService(
      entityServiceSpy,
      httpServiceSpyObj,
    );

    setUpAccountServices
      .get(expectedSetUpCode.code)
      .subscribe((user) => expect(user).toEqual(expectedUserDetails), fail);
    expect(entityServiceSpy.get).toHaveBeenCalledTimes(1);
  }));

  it('should post the expected User Details', fakeAsync(() => {
    setUpAccountServices = TestBed.inject<SetUpAccountService>(
      SetUpAccountService,
    );
    const httpTest = TestBed.inject(HttpTestingController);

    setUpAccountServices
      .setUpAccount(expectedUpdateSetUp)
      .subscribe((successResponse: ApiResponse) =>
        expect(successResponse).toEqual(expectedsetUpResponse),
      );

    const httpReq = httpTest.expectOne(`${environment.baseUrl}/users/`);
    expect(httpReq.request.method).toEqual('POST');

    httpReq.flush(expectedsetUpResponse);
    httpTest.verify();
    tick();
  }));

  it('should return an error when the server returns a 400', fakeAsync(() => {
    const errorResponses: ApiResponse = {
      message: 'Bad Request',
    } as ApiResponse;
    const entityServicespy = jasmine.createSpyObj<EntityService<User>>(
      'EntityService',
      {get: throwError(errorResponses)},
    );
    setUpAccountServices = new SetUpAccountService(
      entityServicespy,
      httpServiceSpyObj,
    );

    tick(40);
    setUpAccountServices.get(expectedSetUpCode.code).subscribe(
      () => fail('expected an error'),
      (error: ApiResponse) => {
        expect(error.message).toBe('Bad Request');
      },
    );
  }));
});
