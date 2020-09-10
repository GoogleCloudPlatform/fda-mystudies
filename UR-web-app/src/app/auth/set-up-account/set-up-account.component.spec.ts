import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';
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
  let firstName: DebugElement;
  let lastName: DebugElement;
  let password: DebugElement;
  let confirmPassword: DebugElement;

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
    firstName = fixture.debugElement.query(By.css('[name="firstName"]'));
    lastName = fixture.debugElement.query(By.css('[name="lastName"]'));
    password = fixture.debugElement.query(By.css('[name="password"]'));
    confirmPassword = fixture.debugElement.query(
      By.css('[name="confirmPassword"]'),
    );
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get userDetails on setup code', () => {
    expect(component.user.email).toEqual('superadmin@grr.la');
    expect(component.user.lastName).toEqual('Dsouza');
  });

  it('should show validation error if the input field is empty', async () => {
    const firstNameInputs = firstName.nativeElement as HTMLInputElement;
    const lastNameInputs = lastName.nativeElement as HTMLInputElement;
    const passwordInputs = password.nativeElement as HTMLInputElement;
    const confirmPasswordInputs = confirmPassword.nativeElement as HTMLInputElement;
    firstNameInputs.value = '';
    lastNameInputs.value = '';
    passwordInputs.value = '';
    confirmPasswordInputs.value = '';
    passwordInputs.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();
    const errorHelpBlock = fixture.debugElement.query(By.css('.help-block'));
    const errorHelpBlocks = errorHelpBlock.nativeElement as HTMLElement;
    expect(firstNameInputs.required).toBeTruthy();
    expect(lastNameInputs.required).toBeTruthy();
    expect(passwordInputs.required).toBeTruthy();
    expect(confirmPasswordInputs.required).toBeTruthy();
    expect(errorHelpBlocks.innerText).toEqual('Please fill out this field.');
  });

  it('should show validation error if the input field exceeds max charecter', async () => {
    fixture.detectChanges();
    const firstNameInputs = firstName.nativeElement as HTMLInputElement;
    firstNameInputs.value = 'checking max charecter';
    firstNameInputs.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();
    const errorMsg = fixture.debugElement.query(By.css('.validation-error'));
    const errorHelpBlock = fixture.debugElement.query(
      By.css('.help-block.with-errors'),
    );
    const errorHelpBlocks = errorHelpBlock.nativeElement as HTMLElement;
    expect(errorHelpBlock).toBeTruthy();
    expect(errorMsg).toBeTruthy();
    expect(errorHelpBlocks.innerText).toEqual(
      'Please enter atleast 3 - 15 alphabets with no space.',
    );
  });

  it('should show validation error if the input field with less than 3 character', async () => {
    fixture.detectChanges();
    const firstNameInputs = firstName.nativeElement as HTMLInputElement;
    firstNameInputs.value = 'ca';
    firstNameInputs.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();
    const errorMsg = fixture.debugElement.query(By.css('.validation-error'));
    const errorHelpBlock = fixture.debugElement.query(
      By.css('.help-block.with-errors'),
    );
    const errorHelpBlocks = errorHelpBlock.nativeElement as HTMLElement;
    expect(errorHelpBlock).toBeTruthy();
    expect(errorMsg).toBeTruthy();
    expect(errorHelpBlocks.innerText).toEqual(
      'Please enter atleast 3 - 15 alphabets with no space.',
    );
  });

  it('should show validation error if the password and confirm password mismatches', async () => {
    const passwordInputs = password.nativeElement as HTMLInputElement;
    const confirmPasswordInputs = confirmPassword.nativeElement as HTMLInputElement;
    passwordInputs.value = 'Password@123';
    confirmPasswordInputs.value = 'Password@12345';
    passwordInputs.dispatchEvent(new Event('input'));
    confirmPasswordInputs.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
    const errorMsg = fixture.debugElement.query(By.css('.validation-error'));
    const errorHelpBlock = fixture.debugElement.query(
      By.css('.help-block.with-errors'),
    );
    const errorHelpBlocks = errorHelpBlock.nativeElement as HTMLElement;
    expect(errorHelpBlock).toBeTruthy();
    expect(errorMsg).toBeTruthy();
    expect(errorHelpBlocks.innerText).toEqual(
      'Entered Password does not match.',
    );
  });

  it('should setup account with valid inputs', fakeAsync(async () => {
    fixture.detectChanges();
    component.user.password = 'Abcd@123456';
    const firstNameInputs = firstName.nativeElement as HTMLInputElement;
    const lastNameInputs = lastName.nativeElement as HTMLInputElement;
    const submitButton = updateUser.nativeElement as HTMLInputElement;
    const passwordInputs = password.nativeElement as HTMLInputElement;
    const confirmPasswordInputs = confirmPassword.nativeElement as HTMLInputElement;
    firstNameInputs.value = 'kamin';
    lastNameInputs.value = 'Dsouza';
    passwordInputs.value = 'Abcd@123456';
    confirmPasswordInputs.value = 'Abcd@123456';
    dispatchEvent(new Event('input'));
    fixture.detectChanges();
    tick();
    submitButton.click();
    fixture.detectChanges();
    await fixture.whenStable();
    expect(component.user.firstName).toEqual(expectedUserDetails.firstName);
    expect(component.user.lastName).toEqual(expectedUserDetails.lastName);
    expect(component.user.password).toEqual('Abcd@123456');
  }));
});
