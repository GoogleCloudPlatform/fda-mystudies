import {async, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {SiteCoordinatorComponent} from './sitecoordinator.component';

describe('SitecoordinatorComponent', () => {
  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [SiteCoordinatorComponent],
    }).compileComponents();
  }));

  it('should create', () => {
    const fixture = TestBed.createComponent(SiteCoordinatorComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
