import {TestBed} from '@angular/core/testing';

import {ManageAccountService} from './manage-account.service';

describe('ManageAccountService', () => {
  let service: ManageAccountService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManageAccountService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
