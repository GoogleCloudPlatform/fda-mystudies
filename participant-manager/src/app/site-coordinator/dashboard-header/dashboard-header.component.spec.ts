import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DashboardHeaderComponent} from './dashboard-header.component';
import {RouterTestingModule} from '@angular/router/testing';

describe('DashboardHeaderComponent', () => {
  let component: DashboardHeaderComponent;
  let fixture: ComponentFixture<DashboardHeaderComponent>;

  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [DashboardHeaderComponent],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(DashboardHeaderComponent);
        component = fixture.componentInstance;
      });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
