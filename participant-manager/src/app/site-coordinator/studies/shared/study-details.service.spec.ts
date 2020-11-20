import {TestBed, fakeAsync, tick} from '@angular/core/testing';
import {StudyDetailsService} from './study-details.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {EntityService} from '../../../service/entity.service';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {throwError, of} from 'rxjs';
import {StudyDetails} from './study-details';
import * as expectedResult from 'src/app/entity/mock-studies-data';
import {HttpClient} from '@angular/common/http';
import {ToastrModule} from 'ngx-toastr';

describe('StudyDetailsService', () => {
  let studyDetailsService: StudyDetailsService;
  let httpServiceSpyObj: jasmine.SpyObj<HttpClient>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SiteCoordinatorModule,
        RouterTestingModule.withRoutes([]),
        ToastrModule.forRoot({
          positionClass: 'toast-top-center',
          preventDuplicates: true,
          enableHtml: true,
        }),
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [StudyDetailsService, EntityService],
    });
  });

  it('should be created', () => {
    const service: StudyDetailsService = TestBed.get(
      StudyDetailsService,
    ) as StudyDetailsService;
    expect(service).toBeTruthy();
  });

  it('should return expected study participant`s details', fakeAsync(() => {
    const entityServiceSpy = jasmine.createSpyObj<EntityService<StudyDetails>>(
      'EntityService',
      {get: of(expectedResult.expectedStudiesDetails)},
    );
      const httpServicespyobj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      get: of(expectedResult.expectedStudiesDetails),
    });
    studyDetailsService = new StudyDetailsService(
      entityServiceSpy,
      httpServicespyobj,
    );

    studyDetailsService
      .getStudyDetails(expectedResult.expectedStudyId.id.toString())
      .subscribe(
        (studyDetail) =>
          expect(studyDetail).toEqual(
            expectedResult.expectedStudiesDetails,
            'expected Studies Participant List',
          ),
        fail,
      );
    expect(httpServicespyobj.get).toHaveBeenCalledTimes(1);
  }));

  it('should return an error when the server returns a 400', fakeAsync(() => {
    const errorResponses: ApiResponse = {
      message: 'Bad Request',
    } as ApiResponse;


    const entitiyServiceSpy = jasmine.createSpyObj<EntityService<StudyDetails>>(
      'EntityService',
      {get: throwError(errorResponses)},
    );

     const httpServicespyobj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      get: throwError(errorResponses),
    });
    studyDetailsService = new StudyDetailsService(
      entitiyServiceSpy,
      httpServicespyobj,
    );

    tick(40);
    studyDetailsService
      .getStudyDetails(expectedResult.expectedStudyId.id.toString())
      .subscribe(
        () => fail('expected an error'),
        (error: ApiResponse) => {
          expect(error.message).toBe('Bad Request');
        },
      );
  }));

  it('should get the updated target enrollment data', () => {
    const entityServicespyobj = jasmine.createSpyObj<
      EntityService<StudyDetails>
    >('EntityService', ['get']);

    const httpServicespyobj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      patch: of(),
    });
    studyDetailsService = new StudyDetailsService(
      entityServicespyobj,
      httpServicespyobj,
    );

    httpServicespyobj.patch.and.returnValue(
      of(expectedResult.expectedResponse),
    );

    studyDetailsService
      .updateTargetEnrollment(
        expectedResult.expectedTargetEnrollment,
        expectedResult.expectedStudiesDetails.participantRegistryDetail.studyId.toString(),
      )
      .subscribe(
        (successResponse: ApiResponse) =>
          expect(successResponse).toEqual(
            expectedResult.expectedResponse,
            '{code:MSG_013,message:Target Enrollment updated successfully}',
          ),
        fail,
      );

    expect(httpServicespyobj.patch.calls.count()).toBe(1, 'one call');
  });
});
