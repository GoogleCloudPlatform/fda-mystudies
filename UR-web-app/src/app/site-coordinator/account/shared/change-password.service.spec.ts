import {TestBed} from '@angular/core/testing';

import {ChangePasswordService} from './change-password.service';

describe('ChangePasswordService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ChangePasswordService = TestBed.get(ChangePasswordService);
    expect(service).toBeTruthy();
  });
});
