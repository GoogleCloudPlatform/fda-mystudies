import {TestBed, fakeAsync} from '@angular/core/testing';
import {SiteDetailsService} from './site-details.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {EntityService} from '../../../service/entity.service';
import {of} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {UpdateInviteResponse} from '../../participant-details/participant-details';
import {SiteParticipants} from '../shared/site-detail.model';
import * as expectedResult from '../../../entity/mock-participant-data';
import {ApiResponse} from '../../../entity/api.response.model';
import {expectedSiteParticipantDetails} from '../../../entity/mock-sitedetail-data';
import {OnboardingStatus} from 'src/app/shared/enums';
describe('SiteDetailsService', () => {
  let participantDetailsService: SiteDetailsService;
  let httpServiceSpyObj: jasmine.SpyObj<HttpClient>;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SiteCoordinatorModule,
        RouterTestingModule.withRoutes([]),
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [SiteDetailsService, EntityService],
    });
  });

  it('should be created', () => {
    const service: SiteDetailsService = TestBed.get(
      SiteDetailsService,
    ) as SiteDetailsService;
    expect(service).toBeTruthy();
  });
  it('should get the site participant details for site id', fakeAsync(() => {
    const entityServiceSpy = jasmine.createSpyObj<
      EntityService<SiteParticipants>
    >('EntityService', {get: of(expectedSiteParticipantDetails)});

    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      get: of(expectedSiteParticipantDetails),
    });
    participantDetailsService = new SiteDetailsService(
      entityServiceSpy,
      httpServiceSpyObj,
    );
    participantDetailsService
      .get(expectedResult.expectedSiteId.siteId, OnboardingStatus.All)
      .subscribe(
        (participant) =>
          expect(participant).toEqual(
            expectedSiteParticipantDetails,
            'expected participant details',
          ),
        fail,
      );
    expect(httpServiceSpyObj.get).toHaveBeenCalledTimes(1);
  }));

  it('should change the status Enable/Disable invitation', () => {
    const entityServiceSpyObj = jasmine.createSpyObj<
      EntityService<SiteParticipants>
    >('EntityService', ['post']);

    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      patch: of(expectedResult.expectedToggleResponse),
    });

    participantDetailsService = new SiteDetailsService(
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
      EntityService<SiteParticipants>
    >('EntityService', ['post']);

    const httpServiceSpyObj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      post: of(expectedResult.expectedSendInviteResponse),
    });

    participantDetailsService = new SiteDetailsService(
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
});
