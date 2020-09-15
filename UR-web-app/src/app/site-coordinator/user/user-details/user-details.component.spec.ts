import {
  async,
  ComponentFixture,
  TestBed,
  fakeAsync,
} from '@angular/core/testing';

import {UserDetailsComponent} from './user-details.component';
import {
  BrowserAnimationsModule,
  NoopAnimationsModule,
} from '@angular/platform-browser/animations';
import {HttpClientModule} from '@angular/common/http';
import {of} from 'rxjs';
import {UserModule} from '../user.module';
import {UserService} from '../shared/user.service';
import {RouterTestingModule} from '@angular/router/testing';
import {expectedManageUserDetails} from 'src/app/entity/mock-users-data';
describe('UserDetailsComponent', () => {
  let component: UserDetailsComponent;
  let fixture: ComponentFixture<UserDetailsComponent>;

  beforeEach(async(async () => {
    const userServiceSpy = jasmine.createSpyObj<UserService>('UserService', {
      getUserDetails: of(expectedManageUserDetails),
    });
    await TestBed.configureTestingModule({
      declarations: [UserDetailsComponent],
      imports: [
        UserModule,
        BrowserAnimationsModule,
        NoopAnimationsModule,
        RouterTestingModule.withRoutes([]),
        HttpClientModule,
      ],
      providers: [{provide: UserService, useValue: userServiceSpy}],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(UserDetailsComponent);
        component = fixture.componentInstance;
      });
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('after get user details', () => {
    beforeEach(async(() => {
      fixture.detectChanges();
      void fixture.whenStable().then(() => {
        fixture.detectChanges();
      });
    }));

    it('should get the user details via refresh function', fakeAsync(() => {
      expect(component.user).toBe(expectedManageUserDetails.user);
    }));
  });
});
