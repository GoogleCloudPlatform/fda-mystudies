import {
  async,
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NO_ERRORS_SCHEMA, DebugElement} from '@angular/core';
import {By} from '@angular/platform-browser';
import {of} from 'rxjs';
import {AuthService} from 'src/app/service/auth.service';
import {HttpClientModule} from '@angular/common/http';
import {EntityService} from 'src/app/service/entity.service';
import {ForgotPasswordService} from '../shared/forgot-password.service';
import {ToastrModule} from 'ngx-toastr';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ModalModule} from 'ngx-bootstrap/modal';
import {RouterTestingModule} from '@angular/router/testing';
import {ForgotPasswordComponent} from './forgot-password.component';
import {expectedForgotEmailResponse} from 'src/app/entity/mock-user-data';

describe('ForgotPasswordComponent', () => {
  let component: ForgotPasswordComponent;
  let fixture: ComponentFixture<ForgotPasswordComponent>;
  let email: DebugElement;
  let submitEmail: DebugElement;

  beforeEach(async(async () => {
    const setUpAccountServiceSpy = jasmine.createSpyObj<ForgotPasswordService>(
      'forgotPasswordService',
      {resetPassword: of(expectedForgotEmailResponse)},
    );

    await TestBed.configureTestingModule({
      declarations: [ForgotPasswordComponent],
      imports: [
        RouterTestingModule,
        HttpClientModule,
        ReactiveFormsModule,
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
        {provide: ForgotPasswordService, useValue: setUpAccountServiceSpy},
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ForgotPasswordComponent);
    component = fixture.componentInstance;
    email = fixture.debugElement.query(By.css('[name="email"]'));
    submitEmail = fixture.debugElement.query(By.css('[name="submitemail"]'));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show validation error if the email input field is empty', async () => {
    const emailInputs = email.nativeElement as HTMLInputElement;
    emailInputs.value = '';
    emailInputs.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();
    const errorHelpBlock = fixture.debugElement.query(
      By.css('.help-block.with-errors'),
    );
    const errorHelpBlocks = errorHelpBlock.nativeElement as HTMLElement;
    expect(emailInputs.required).toBeTruthy();
    expect(errorHelpBlocks.innerText).toEqual('Enter your registered email.');
  });

  it('should show validation error if the email is invalid', async () => {
    const emailInputs = email.nativeElement as HTMLInputElement;
    emailInputs.value = 'emailTest.com';
    emailInputs.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    await fixture.whenStable();
    const errorMsg = fixture.debugElement.query(By.css('.validation-error'));
    const errorHelpBlock = fixture.debugElement.query(
      By.css('.help-block.with-errors'),
    );
    const errorHelpBlocks = errorHelpBlock.nativeElement as HTMLElement;
    expect(errorHelpBlock).toBeTruthy();
    expect(errorMsg).toBeTruthy();
    expect(errorHelpBlocks.innerText).toEqual('Email is invalid.');
  });

  it('should update the email on forgotPassword button submit', fakeAsync(() => {
    const emailInputs = email.nativeElement as HTMLInputElement;
    const submitButton = submitEmail.nativeElement as HTMLInputElement;
    emailInputs.value = 'test@grr.la';
    emailInputs.dispatchEvent(new Event('input'));
    dispatchEvent(new Event('input'));
    const errorMsg = fixture.debugElement.query(By.css('.validation-error'));
    const errorHelpBlock = fixture.debugElement.query(
      By.css('.help-block.with-errors'),
    );
    fixture.detectChanges();
    tick();
    const submitSpy = spyOn(component, 'forgotPassword');
    submitButton.click();
    fixture.detectChanges();
    expect(errorHelpBlock).toBeFalsy();
    expect(errorMsg).toBeFalsy();
    expect(submitSpy).toHaveBeenCalledTimes(1);
  }));
});
