import {TestBed} from '@angular/core/testing';

import {ManageLocationService} from './manage-location.service';

describe('ManageLocationService', () => {
  let service: ManageLocationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManageLocationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
