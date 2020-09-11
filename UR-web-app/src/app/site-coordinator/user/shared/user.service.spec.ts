import {TestBed, fakeAsync, tick} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {UserService} from './user.service';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {addUserResponse, addUserRequest} from 'src/app/entity/mock-app-details';
import {of, throwError} from 'rxjs';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {
  expectedManageUsers,
  expectedManageUserDetails,
} from 'src/app/entity/mock-users-data';

describe('UserService', () => {
  let service: UserService;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      schemas: [NO_ERRORS_SCHEMA],
    });
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return expected user list', fakeAsync(() => {
    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      get: of(expectedManageUsers),
    });
    service = new UserService(httpServiceSpyObj);
    service
      .getUsers()
      .subscribe(
        (manageUser) =>
          expect(manageUser).toEqual(
            expectedManageUsers,
            'expected user`s List',
          ),
        fail,
      );
    expect(httpServiceSpyObj.get).toHaveBeenCalledTimes(1);
  }));

  it('should return an error when the server returns a 400', fakeAsync(() => {
    const errorResponses: ApiResponse = {
      message: 'Bad Request',
    } as ApiResponse;
    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      get: throwError(errorResponses),
    });
    service = new UserService(httpServiceSpyObj);
    tick(40);
    service.getUsers().subscribe(
      () => fail('expected an error'),
      (error: ApiResponse) => {
        expect(error.message).toBe('Bad Request');
      },
    );
  }));

  it('should return expected user`s details', fakeAsync(() => {
    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      get: of(expectedManageUserDetails),
    });
    service = new UserService(httpServiceSpyObj);

    service
      .getUserDetails(expectedManageUserDetails.user.id)
      .subscribe(
        (userDetails) =>
          expect(userDetails).toEqual(
            expectedManageUserDetails,
            'expected user`s details',
          ),
        fail,
      );
    expect(httpServiceSpyObj.get).toHaveBeenCalledTimes(1);
  }));

  it('should post the expected new user data', () => {
    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      post: of(addUserResponse),
    });
    service = new UserService(httpServiceSpyObj);

    service
      .add(addUserRequest)
      .subscribe(
        (successResponse: ApiResponse) =>
          expect(successResponse).toEqual(addUserResponse),
        fail,
      );
    expect(httpServiceSpyObj.post).toHaveBeenCalledTimes(1);
  });
});
