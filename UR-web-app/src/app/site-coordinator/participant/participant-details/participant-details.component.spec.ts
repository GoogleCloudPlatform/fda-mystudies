import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ParticipantDetailsComponent} from './participant-detail.component';

describe('ParticipantDetailComponent', () => {
  let component: ParticipantDetailsComponent;
  let fixture: ComponentFixture<ParticipantDetailsComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [ParticipantDetailsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ParticipantDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
