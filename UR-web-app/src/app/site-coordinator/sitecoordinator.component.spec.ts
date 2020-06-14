import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SiteCoordinatorComponent} from './sitecoordinator.component';

describe('SitecoordinatorComponent', () => {
  let component: SiteCoordinatorComponent;
  let fixture: ComponentFixture<SiteCoordinatorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SiteCoordinatorComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SiteCoordinatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
