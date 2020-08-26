import {
  async,
  ComponentFixture,
  TestBed,
  fakeAsync,
} from '@angular/core/testing';
import {
  BrowserAnimationsModule,
  NoopAnimationsModule,
} from '@angular/platform-browser/animations';

import {HttpClientModule} from '@angular/common/http';
import {of} from 'rxjs';
import {UserModule} from '../user.module';
import {UserService} from '../shared/user.service';
import {UserListComponent} from './user-list.component';
import {RouterTestingModule} from '@angular/router/testing';
import {ManageUsers} from '../shared/manage-user';
import {expectedManageUsers} from 'src/app/entity/mock-users-data';

describe('ManageUserListComponent', () => {
  let component: UserListComponent;
  let fixture: ComponentFixture<UserListComponent>;

  beforeEach(async(async () => {
    const userServiceSpy = jasmine.createSpyObj<UserService>('UserService', {
      getUsers: of(expectedManageUsers),
    });
    await TestBed.configureTestingModule({
      declarations: [UserListComponent],
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
        fixture = TestBed.createComponent(UserListComponent);
        component = fixture.componentInstance;
      });
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should NOT have user`s list before ngOnInit', () => {
    component.manageUser$.pipe().subscribe((manageUser: ManageUsers) => {
      expect(manageUser.users.length).toBe(
        0,
        'should NOT have user`s participant list before ngOnInit',
      );
    });
  });

  describe('after get users', () => {
    beforeEach(async(() => {
      fixture.detectChanges();
      void fixture.whenStable().then(() => {
        fixture.detectChanges();
      });
    }));

    it('should get the list of users via refresh function', fakeAsync(() => {
      component.manageUser$.subscribe((manageUser) => {
        expect(manageUser.users.length).toEqual(
          expectedManageUsers.users.length,
        );
      });
    }));
  });
});
