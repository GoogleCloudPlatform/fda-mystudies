import {TestBed} from '@angular/core/testing';
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
import {AuthService} from '../../../service/auth.service';
describe('AccountService', () => {
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
});
