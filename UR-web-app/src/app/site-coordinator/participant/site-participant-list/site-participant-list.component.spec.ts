import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SiteParticipantListComponent} from './site-participant-list.component';

describe('SiteParticipantListComponent', () => {
  let component: SiteParticipantListComponent;
  let fixture: ComponentFixture<SiteParticipantListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SiteParticipantListComponent],
    })
        .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SiteParticipantListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
