import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {UserService} from './user.service';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {addUserResponse, addUserRequest} from 'src/app/entity/mock-app-details';
import {of} from 'rxjs';
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
