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
import {By} from '@angular/platform-browser';
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
    const changePasswordSpy = spyOn(component, 'changePassword');
    expect(component.resetPasswordForm.invalid).toBe(true);
    fixture.detectChanges();
    tick();
    fixture.debugElement
      .query(By.css('form'))
      .triggerEventHandler('submit', null);
    fixture.detectChanges();
    await fixture.whenStable();
    expect(changePasswordSpy).toHaveBeenCalledTimes(1);
  }));

  it('should validate the form when input is not provided', () => {
    expect(component.resetPasswordForm.invalid).toBe(true);
  });
});
