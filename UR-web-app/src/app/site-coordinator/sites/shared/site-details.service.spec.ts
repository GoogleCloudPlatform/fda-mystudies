import {TestBed} from '@angular/core/testing';
import {SiteDetailsService} from './site-details.service';

describe('SiteDetailsService', () => {
  let service: SiteDetailsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SiteDetailsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
