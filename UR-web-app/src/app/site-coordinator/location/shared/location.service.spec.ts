import {TestBed} from '@angular/core/testing';

import {LocationService} from './location.service';
import {SiteCoordinatorModule} from '../../site-coordinator.module';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {RouterTestingModule} from '@angular/router/testing';
import {BsModalService, BsModalRef} from 'ngx-bootstrap/modal';
import {EntityService} from '../../../service/entity.service';
import {HttpClientModule} from '@angular/common/http';

describe('LocationService', () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      imports: [
        SiteCoordinatorModule,
        HttpClientModule,
        RouterTestingModule.withRoutes([]),
      ],
      schemas: [NO_ERRORS_SCHEMA],
      providers: [LocationService, EntityService, BsModalService, BsModalRef],
    }),
  );

  it('should be created', () => {
    const service: LocationService = TestBed.get(
      LocationService,
    ) as LocationService;
    expect(service).toBeTruthy();
  });
});
