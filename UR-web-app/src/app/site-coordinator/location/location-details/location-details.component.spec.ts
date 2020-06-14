import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LocationDetailsComponent} from './location-details.component';

describe('LocationDetailsComponent', () => {
  let component: LocationDetailsComponent;
  let fixture: ComponentFixture<LocationDetailsComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [LocationDetailsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LocationDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
