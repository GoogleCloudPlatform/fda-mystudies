import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AppParticipantListComponent} from './app-participant-list.component';

describe('AppParticipantListComponent', () => {
  let component: AppParticipantListComponent;
  let fixture: ComponentFixture<AppParticipantListComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [AppParticipantListComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AppParticipantListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
