import {TestBed, fakeAsync, tick} from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import {SiteCoordinatorModule} from 'src/app/site-coordinator/site-coordinator.module';
import {RouterTestingModule} from '@angular/router/testing';
import {EntityService} from 'src/app/service/entity.service';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {environment} from '@environment';

import {ForgotPasswordService} from './forgot-password.service';
import {
  expectedForgotEmail,
  expectedForgotEmailResponse,
} from 'src/app/entity/mock-user-data';

describe('ForgotPasswordService', () => {
  let forgotPasswordService: ForgotPasswordService;

  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SiteCoordinatorModule,
        RouterTestingModule.withRoutes([]),
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [ForgotPasswordService, EntityService],
    }),
  );

  it('should be created', () => {
    const service: ForgotPasswordService = TestBed.get(
      ForgotPasswordService,
    ) as ForgotPasswordService;
    expect(service).toBeTruthy();
  });

  it('should post the expected user email', fakeAsync(() => {
    forgotPasswordService = TestBed.inject<ForgotPasswordService>(
      ForgotPasswordService,
    );
    const httpTest = TestBed.inject(HttpTestingController);

    forgotPasswordService
      .resetPassword(expectedForgotEmail)
      .subscribe((successResponse: ApiResponse) =>
        expect(successResponse).toEqual(expectedForgotEmailResponse),
      );

    const httpReq = httpTest.expectOne(
      `${environment.authServerUrl}/users/reset_password`,
    );
    expect(httpReq.request.method).toEqual('POST');

    httpReq.flush(expectedForgotEmailResponse);
    httpTest.verify();
  }));

  it('should return an error when the server returns a 400', fakeAsync(() => {
    forgotPasswordService = TestBed.inject<ForgotPasswordService>(
      ForgotPasswordService,
    );
    const httpTest = TestBed.inject(HttpTestingController);

    tick(40);
    forgotPasswordService.resetPassword(expectedForgotEmail).subscribe(
      () => fail('expected an error'),
      (error: ApiResponse) => {
        expect(error.message).toBe('Bad Request');
      },
    );
    const httpReq = httpTest.expectOne(
      `${environment.authServerUrl}/users/reset_password`,
    );
    expect(httpReq.request.method).toEqual('POST');
    httpTest.verify();
  }));
});
