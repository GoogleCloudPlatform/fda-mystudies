import {TestBed, fakeAsync} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {UserService} from './user.service';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {
  expectedAppDetails,
  addUserResponse,
  addUserRequest,
} from 'src/app/entity/mock-app-details';
import {of, throwError} from 'rxjs';
import {ApiResponse} from 'src/app/entity/api.response.model';
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

  it('should return apps list for the user creation', () => {
    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      get: of(expectedAppDetails),
    });
    service = new UserService(httpServiceSpyObj);

    service
      .getAllApps()
      .subscribe(
        (appDetails) =>
          expect(appDetails).toEqual(
            expectedAppDetails,
            'expected App Details',
          ),
        fail,
      );
    expect(httpServiceSpyObj.get).toHaveBeenCalledTimes(1);
  });

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

  it('should return an error when the server returns a error status code', fakeAsync(() => {
    const errorResponse = {
      message: 'User does not exist',
    } as ApiResponse;
    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      get: throwError(errorResponse),
    });
    service = new UserService(httpServiceSpyObj);
    service.getAllApps().subscribe(
      () => fail('expected an error, not app details'),
      (error: ApiResponse) => {
        expect(error.message).toContain(errorResponse.message);
      },
    );
  }));
});
