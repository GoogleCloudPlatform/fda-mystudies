import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {StudyParticipantListComponent} from './study-participant-list.component';

describe('StudyParticipantListComponent', () => {
  let component: StudyParticipantListComponent;
  let fixture: ComponentFixture<StudyParticipantListComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [StudyParticipantListComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudyParticipantListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
