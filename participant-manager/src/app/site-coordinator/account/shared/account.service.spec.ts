import {TestBed, fakeAsync} from '@angular/core/testing';
import {AccountService} from './account.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {EntityService} from '../../../service/entity.service';
import {of} from 'rxjs';
import * as expectedResult from 'src/app/entity/mock-profile-data';
import {Profile, UpdateProfile} from './profile.model';
import {AuthService} from '../../../service/auth.service';
import {HttpClient} from '@angular/common/http';
import {ApiResponse} from 'src/app/entity/api.response.model';
describe('AccountService', () => {
  let accountService: AccountService;
  let httpServiceSpyObj: jasmine.SpyObj<HttpClient>;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SiteCoordinatorModule,
        RouterTestingModule.withRoutes([]),
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [AccountService, EntityService, AuthService],
    });
  });

  it('should fetch profile details', fakeAsync(() => {
    const entityServiceSpy = jasmine.createSpyObj<EntityService<Profile>>(
      'EntityService',
      {get: of(expectedResult.expectedProfiledataResposnse)},
    );
    const authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', {
      getAuthUserId: expectedResult.expectedAuthUserId,
    });
    accountService = new AccountService(
      entityServiceSpy,
      httpServiceSpyObj,
      authServiceSpy,
    );
    accountService
      .fetchUserProfile()
      .subscribe(
        (profile) =>
          expect(profile).toEqual(
            expectedResult.expectedProfiledataResposnse,
            'expected participant details',
          ),
        fail,
      );
    expect(entityServiceSpy.get).toHaveBeenCalledTimes(1);
  }));
  it('should update profile after submit', fakeAsync(() => {
    const entityServiceSpy = jasmine.createSpyObj<EntityService<Profile>>(
      'EntityService',
      {get: of(expectedResult.expectedProfiledataResposnse)},
    );
    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      put: of(expectedResult.expectedUpdateResponse),
    });
    const authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', {
      getUserId: expectedResult.expectedAuthUserId,
    });
    accountService = new AccountService(
      entityServiceSpy,
      httpServiceSpyObj,
      authServiceSpy,
    );
    const profileFieldTobEUpdated: UpdateProfile = {
      firstName: 'John',
      lastName: 'Doe',
    };
    accountService
      .updateUserProfile(profileFieldTobEUpdated)
      .subscribe(
        (successResponse: ApiResponse) =>
          expect(successResponse).toEqual(
            expectedResult.expectedUpdateResponse,
          ),
        fail,
      );
    expect(httpServiceSpyObj.put).toHaveBeenCalledTimes(1);
  }));
});
