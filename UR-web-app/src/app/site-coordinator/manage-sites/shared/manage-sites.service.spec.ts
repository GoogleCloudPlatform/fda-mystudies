import {TestBed} from '@angular/core/testing';

import {ManageSitesService} from './manage-sites.service';

describe('ManageSitesService', () => {
  let service: ManageSitesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManageSitesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
