import {TestBed, fakeAsync, tick} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {EntityService} from '../../../service/entity.service';
import {ApiResponse} from 'src/app/entity/error.model';
import {throwError, of} from 'rxjs';
import {DashboardModel} from '../shared/dashboard.model';
import {StudiesService} from './studies.service';

describe('StudiesService', () => {
  let service: StudiesService;
  const expectedStudies = [
    {
      appId: 'dsdssd',
      appInfoId: 2,
      customId: 'dsasd',
      enrolledCount: 3,
      enrollmentPercentage: 25,
      id: 12,
      invitedCount: 2,
      name: 'dsadasd',
      sites: [],
      studyPermission: 2,
      totalSitesCount: 2,
    },
  ];
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SiteCoordinatorModule,
        RouterTestingModule.withRoutes([]),
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [StudiesService, EntityService, BsModalService, BsModalRef],
    });
    // service = TestBed.inject(StudiesService);
  });

  it('should be created', () => {
    const service: StudiesService = TestBed.get(
      StudiesService,
    ) as StudiesService;
    expect(service).toBeTruthy();
  });

  it('should return expected Studies List', () => {
    const entityServicespy = jasmine.createSpyObj<
      EntityService<DashboardModel>
    >('EntityService', ['getCollection']);
    locationService = new LocationService(entityServicespy);

    entityServicespy.getCollection.and.returnValue(of(expectedStudies));
    studyService
      .getLocations()
      .subscribe(
        (Locations) =>
          expect(Locations).toEqual(expectedLocations, 'expected Locations'),
        fail,
      );

    expect(entityServicespy.getCollection.calls.count()).toBe(1, 'one call');
  });
});
