import {TestBed, fakeAsync, tick} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SiteCoordinatorModule} from '../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {EntityService} from '../../service/entity.service';
import {throwError, of} from 'rxjs';
import {ParticipantDetailsService} from './participant-details.service';
import {HttpClient} from '@angular/common/http';
import {Participant, UpdateInviteResponse} from './participant-details';
import * as expectedResult from 'src/app/entity/mock-participant-data';
import {ApiResponse} from 'src/app/entity/api.response.model';

describe('ParticipantDetailsService', () => {
  let participantDetailsService: ParticipantDetailsService;
  let httpServiceSpyObj: jasmine.SpyObj<HttpClient>;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SiteCoordinatorModule,
        RouterTestingModule.withRoutes([]),
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [ParticipantDetailsService, EntityService],
    });
  });

  it('should be created', () => {
    const service: ParticipantDetailsService = TestBed.get(
      ParticipantDetailsService,
    ) as ParticipantDetailsService;
    expect(service).toBeTruthy();
  });

  it('should get the participant details for id', fakeAsync(() => {
    const entityServiceSpy = jasmine.createSpyObj<EntityService<Participant>>(
      'EntityService',
      {get: of(expectedResult.expectedParticipantDetails)},
    );
    participantDetailsService = new ParticipantDetailsService(
      entityServiceSpy,
      httpServiceSpyObj,
    );

    participantDetailsService
      .get(expectedResult.expectedParticipantId.id)
      .subscribe(
        (participant) =>
          expect(participant).toEqual(
            expectedResult.expectedParticipantDetails,
            'expected participant details',
          ),
        fail,
      );

    expect(entityServiceSpy.get).toHaveBeenCalledTimes(1);
  }));

  it('should change the status Enable/Disable invitation', () => {
    const entityServiceSpyObj = jasmine.createSpyObj<
      EntityService<Participant>
    >('EntityService', ['post']);

    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      patch: of(expectedResult.expectedToggleResponse),
    });

    participantDetailsService = new ParticipantDetailsService(
      entityServiceSpyObj,
      httpServiceSpyObj,
    );

    participantDetailsService
      .toggleInvitation(
        expectedResult.expectedSiteId.siteId,
        expectedResult.expectedToggleInvitation,
      )
      .subscribe(
        (successResponse: ApiResponse) =>
          expect(successResponse).toEqual(
            expectedResult.expectedToggleResponse,
            '{message:Site status updated successfully}',
          ),
        fail,
      );
    expect(httpServiceSpyObj.patch).toHaveBeenCalledTimes(1);
  });

  it('should send the invitation', () => {
    const entityServiceSpyObj = jasmine.createSpyObj<
      EntityService<Participant>
    >('EntityService', ['post']);

    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      post: of(expectedResult.expectedSendInviteResponse),
    });

    participantDetailsService = new ParticipantDetailsService(
      entityServiceSpyObj,
      httpServiceSpyObj,
    );

    participantDetailsService
      .sendInvitation(
        expectedResult.expectedSiteId.siteId,
        expectedResult.expectedSendInvitation,
      )
      .subscribe(
        (successResponse: UpdateInviteResponse) =>
          expect(successResponse).toEqual(
            expectedResult.expectedSendInviteResponse,
            '{message:Participant invited successfully}',
          ),
        fail,
      );
    expect(httpServiceSpyObj.post).toHaveBeenCalledTimes(1);
  });

  it('should return an error when the server returns a 400', fakeAsync(() => {
    const errorResponses: ApiResponse = {
      message: 'Bad Request',
    } as ApiResponse;
    const entityServiceSpy = jasmine.createSpyObj<EntityService<Participant>>(
      'EntityService',
      {get: throwError(errorResponses)},
    );
    participantDetailsService = new ParticipantDetailsService(
      entityServiceSpy,
      httpServiceSpyObj,
    );

    tick(40);
    participantDetailsService
      .get(expectedResult.expectedParticipantId.id)
      .subscribe(
        () => fail('expected an error'),
        (error: ApiResponse) => {
          expect(error.message).toBe('Bad Request');
        },
      );
  }));
});
