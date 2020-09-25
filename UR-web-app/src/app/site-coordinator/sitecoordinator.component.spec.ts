import {async, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {SiteCoordinatorComponent} from './sitecoordinator.component';
import {UserService} from '../service/user.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('SitecoordinatorComponent', () => {
  beforeEach(async(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [SiteCoordinatorComponent],
      providers: [UserService],
    }).compileComponents();
  }));

  it('should create', () => {
    const fixture = TestBed.createComponent(SiteCoordinatorComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
