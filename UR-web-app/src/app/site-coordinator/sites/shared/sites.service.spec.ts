import {TestBed, fakeAsync, tick} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {EntityService} from '../../../service/entity.service';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {throwError, of} from 'rxjs';
import {Study} from '../../studies/shared/study.model';
import {SitesService} from './sites.service';
import {StudiesService} from '../../studies/shared/studies.service';
import * as expectedResult from 'src/app/entity/mock-studies-data';
import {HttpClient} from '@angular/common/http';

describe('SitesService', () => {
  let sitesService: SitesService;
  let studiesServices: StudiesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SiteCoordinatorModule,
        RouterTestingModule.withRoutes([]),
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [SitesService, StudiesService, EntityService],
    });
    sitesService = TestBed.inject(SitesService);
  });

  it('should be created', () => {
    const service: SitesService = TestBed.get(SitesService) as SitesService;
    expect(service).toBeTruthy();
  });

  it('should return an error when the server returns a 400', fakeAsync(() => {
    const errorResponses: ApiResponse = {
      message: 'Bad Request',
    } as ApiResponse;

    const entityServicespy = jasmine.createSpyObj<EntityService<Study>>(
      'EntityService',
      {getCollection: throwError(errorResponses)},
    );
    studiesServices = new StudiesService(entityServicespy);

    tick(40);
    studiesServices.getStudiesWithSites().subscribe(
      () => fail('expected an error'),
      (error: ApiResponse) => {
        expect(error.message).toBe('Bad Request');
      },
    );
  }));

  it('should post the  expected new site data', () => {
    const httpServicespyobj = jasmine.createSpyObj<HttpClient>('HttpClient', {
      post: of(expectedResult.expectedSiteResponse),
    });
    sitesService = new SitesService(httpServicespyobj);

    sitesService
      .add(expectedResult.expectedNewSite)
      .subscribe(
        (succesResponse: ApiResponse) =>
          expect(succesResponse).toEqual(
            expectedResult.expectedSiteResponse,
            '{code:200,message:New site added successfully}',
          ),
        fail,
      );
    expect(httpServicespyobj.post).toHaveBeenCalledTimes(1);
  });
});
