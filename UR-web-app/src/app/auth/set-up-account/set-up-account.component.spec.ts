import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import {
  FormsModule,
  ReactiveFormsModule,
  AbstractControl,
} from '@angular/forms';
import {SetUpAccountComponent} from './set-up-account.component';
import {EntityService} from 'src/app/service/entity.service';
import {SetUpAccountService} from '../shared/set-up-account.service';
import {ToastrModule} from 'ngx-toastr';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ModalModule} from 'ngx-bootstrap/modal';
import {RouterTestingModule} from '@angular/router/testing';
import {AuthService} from 'src/app/service/auth.service';
import {HttpClientModule} from '@angular/common/http';
import {of} from 'rxjs';
import {expectedUserDetails} from 'src/app/entity/mock-user-data';
import {NO_ERRORS_SCHEMA, DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';

describe('SetUpAccountComponent', () => {
  let component: SetUpAccountComponent;
  let fixture: ComponentFixture<SetUpAccountComponent>;
  let updateUser: DebugElement;
  let firstName: AbstractControl;
  let lastName: AbstractControl;
  let password: AbstractControl;
  let confirmPassword: AbstractControl;

  beforeEach(async () => {
    const setUpAccountServiceSpy = jasmine.createSpyObj<SetUpAccountService>(
      'setUpAccountService',
      {get: of(expectedUserDetails)},
    );
    await TestBed.configureTestingModule({
      declarations: [SetUpAccountComponent],
      imports: [
        RouterTestingModule,
        HttpClientModule,
        FormsModule,
        ReactiveFormsModule,
        ModalModule.forRoot(),
        BrowserAnimationsModule,
        ToastrModule.forRoot({
          positionClass: 'toast-top-center',
          preventDuplicates: true,
          enableHtml: true,
        }),
      ],
      providers: [
        EntityService,
        AuthService,
        {provide: SetUpAccountService, useValue: setUpAccountServiceSpy},
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(SetUpAccountComponent);
    component = fixture.componentInstance;
    updateUser = fixture.debugElement.query(By.css('[name="save"]'));
    firstName = component.setupaccountForm.controls['firstName'];
    lastName = component.setupaccountForm.controls['lastName'];
    password = component.setupaccountForm.controls['password'];
    confirmPassword = component.setupaccountForm.controls['confirmPassword'];
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get userDetails on setup code', () => {
    expect(component.setupaccountForm.controls['email'].value).toEqual(
      'superadmin@grr.la',
    );
    expect(component.setupaccountForm.controls['firstName'].value).toEqual(
      'kamin',
    );
    expect(component.setupaccountForm.controls['lastName'].value).toEqual(
      'Dsouza',
    );
  });

  it('should validate the form when input is not provided', () => {
    expect(component.setupaccountForm.invalid).toBe(true);
  });

  it('should register the user when button is submitted', fakeAsync(async () => {
    const toggleChangeSpy = spyOn(component, 'registerUser');
    fixture.detectChanges();
    tick();
    fixture.debugElement
      .query(By.css('form'))
      .triggerEventHandler('submit', null);
    fixture.detectChanges();
    await fixture.whenStable();
    expect(toggleChangeSpy).toHaveBeenCalledTimes(1);
  }));

  it('should setup account with valid inputs', fakeAsync(async () => {
    fixture.detectChanges();
    component.user.password = 'Abcd@123456';
    const firstNameInputs = firstName;
    const lastNameInputs = lastName;
    const submitButton = updateUser.nativeElement as HTMLInputElement;
    const passwordInputs = password;
    const confirmPasswordInputs = confirmPassword;
    firstNameInputs.setValue('kamin');
    lastNameInputs.setValue('Dsouza');
    passwordInputs.setValue('Abcd@123456');
    confirmPasswordInputs.setValue('Abcd@123456');
    dispatchEvent(new Event('input'));
    fixture.detectChanges();
    tick();
    submitButton.click();
    fixture.detectChanges();
    await fixture.whenStable();
    expect(component.setupaccountForm.controls['firstName'].value).toEqual(
      expectedUserDetails.firstName,
    );
    expect(component.setupaccountForm.controls['lastName'].value).toEqual(
      expectedUserDetails.lastName,
    );
    expect(component.setupaccountForm.controls['password'].value).toEqual(
      'Abcd@123456',
    );
  }));
});
