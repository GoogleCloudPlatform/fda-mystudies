import {
  async,
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import {ChangePasswordComponent} from './change-password.component';
import {EntityService} from 'src/app/service/entity.service';
import {AccountService} from '../shared/account.service';
import {of} from 'rxjs';
import {AccountModule} from '../account.module';
import {ToastrModule} from 'ngx-toastr';
import {SiteCoordinatorModule} from '../../../site-coordinator/site-coordinator.module';
import {RouterTestingModule} from '@angular/router/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HttpClientModule} from '@angular/common/http';
import {expectedChangePasswordResponse} from '../../../entity/mock-profile-data';
describe('ChangePasswordComponent', () => {
  let component: ChangePasswordComponent;
  let fixture: ComponentFixture<ChangePasswordComponent>;

  beforeEach(async(async () => {
    const changePasswordServiceSpy = jasmine.createSpyObj<AccountService>(
      'AccountService',
      {
        changePassword: of(expectedChangePasswordResponse),
      },
    );
    await TestBed.configureTestingModule({
      declarations: [ChangePasswordComponent],
      imports: [
        AccountModule,
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        SiteCoordinatorModule,
        ToastrModule.forRoot({
          positionClass: 'toast-top-center',
          preventDuplicates: true,
          enableHtml: true,
        }),
      ],
      providers: [
        EntityService,
        {provide: AccountService, useValue: changePasswordServiceSpy},
      ],
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(ChangePasswordComponent);
        component = fixture.componentInstance;
      });
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangePasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update the password when  button is submitted', fakeAsync(async () => {
    spyOn(component, 'changePassword').and.callThrough();
    component.changePassword();
    component.resetPasswordForm.controls['currentPassword'].setValue(
      'currentPassword',
    );
    component.resetPasswordForm.controls['newPassword'].setValue(
      'Newpassword3241*',
    );
    component.resetPasswordForm.controls['confirmPassword'].setValue(
      'Newpassword3241*',
    );
    fixture.detectChanges();
    expect(component.resetPasswordForm.invalid).toBe(false);
    fixture.detectChanges();
    tick(10000);
    await fixture.whenStable();
    // eslint-disable-next-line @typescript-eslint/unbound-method
    expect(component.changePassword).toHaveBeenCalled();
  }));
  it('should validate the form when input is not provided', () => {
    expect(component.resetPasswordForm.invalid).toBe(true);
  });
});
