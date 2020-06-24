import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LocationListComponent} from './location-list.component';

describe('LocationsListComponent', () => {
  let component: LocationListComponent;
  let fixture: ComponentFixture<LocationListComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      declarations: [LocationListComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LocationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
