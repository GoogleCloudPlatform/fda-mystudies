import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LoadmoreSpinnerComponent} from './loadmore-spinner.component';

describe('LoadmoreSpinnerComponent', () => {
  let component: LoadmoreSpinnerComponent;
  let fixture: ComponentFixture<LoadmoreSpinnerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LoadmoreSpinnerComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoadmoreSpinnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
