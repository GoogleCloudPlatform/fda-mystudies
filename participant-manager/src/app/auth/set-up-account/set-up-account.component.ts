import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Validators} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {SetUpUser} from '../../entity/user';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {SetUpAccountService} from 'src/app/auth/shared/set-up-account.service';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {AuthService} from 'src/app/service/auth.service';
import {SetUpResponse} from '../shared/set-up-account';
import {
  mustMatch,
  newPasswordValidator,
  passwordValidator,
} from 'src/app/_helper/validator';

import {
  ErrorCode,
  getMessage as getErrorMessage,
} from 'src/app/shared/error.codes.enum';

@Component({
  selector: 'app-set-up-account',
  templateUrl: './set-up-account.component.html',
  styleUrls: ['./set-up-account.component.scss'],
})
export class SetUpAccountComponent
  extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  user = {} as SetUpUser;
  setUpCode = '';
  tempRegId = '';
  setupAccountForm: FormGroup;
  passCriteria = '';
  disableButton = false;
  passwordMeterLow = '.25';
  passwordMeterHigh = '.75';
  passwordMeterValue = '.2';
  passwordMeterOptimum = '.8';
  meterStatus = '';
  submitted = false;
  fieldTextType = false;
  serviceName = '';
  consecutiveCharacter = '';
  passwordLength = '';
  userName = '';
  constructor(
    private readonly fb: FormBuilder,
    private readonly setUpAccountService: SetUpAccountService,
    private readonly authService: AuthService,
    private readonly route: ActivatedRoute,
    private readonly toastr: ToastrService,
    private readonly router: Router,
  ) {
    super();
    this.setupAccountForm = fb.group(
      {
        // eslint-disable-next-line @typescript-eslint/unbound-method
        email: ['', Validators.required],
        firstName: [
          '',
          // eslint-disable-next-line @typescript-eslint/unbound-method
          [Validators.required],
        ],
        lastName: [
          '',
          // eslint-disable-next-line @typescript-eslint/unbound-method
          [Validators.required],
        ],
        password: [
          '',
          // eslint-disable-next-line @typescript-eslint/unbound-method
          [Validators.required, passwordValidator()],
        ],
        confirmPassword: [
          '',
          // eslint-disable-next-line @typescript-eslint/unbound-method
          [Validators.required],
        ],
      },
      {
        validator: [
          mustMatch('password', 'confirmPassword'),
          newPasswordValidator('firstName', 'lastName', 'password'),
        ],
      },
    );
    this.onChange();
  }

  toggleFieldTextType() {
    this.fieldTextType = !this.fieldTextType;
  }

  get f() {
    return this.setupAccountForm.controls;
  }
  ngOnInit(): void {
    this.subs.add(
      this.route.params.subscribe((params) => {
        this.setUpCode = params.securityCode as string;
        this.getPreStoredDetails();
      }),
    );
    this.passCriteria = `Your password must be at least 8 characters long    
    and contain lower case, upper case, numeric and
    special characters.`;
  }

  getError(err: ErrorCode): string {
    return getErrorMessage(err);
  }

  getPreStoredDetails(): void {
    this.setUpAccountService.get(this.setUpCode).subscribe((user) => {
      this.setupAccountForm.patchValue(user);
    });
  }

  registerUser(): void {
    const updatedUser: SetUpUser = {
      firstName: String(this.setupAccountForm.controls['firstName'].value),
      lastName: String(this.setupAccountForm.controls['lastName'].value),
      email: String(this.setupAccountForm.controls['email'].value),
      password: String(this.setupAccountForm.controls['password'].value),
    };
    this.subs.add(
      this.setUpAccountService
        .setUpAccount(updatedUser)
        .subscribe((successResponse: SetUpResponse) => {
          this.disableButton = true;
          this.toastr.success(getMessage(successResponse.code));
          sessionStorage.setItem('tempRegId', successResponse.tempRegId);
          sessionStorage.setItem('userId', successResponse.userId);
          this.authService.initSessionStorage();
          setTimeout(() => {
            this.authService.beginLoginConsentFlow();
          }, 1000);
        }),
    );
  }

  onChange(): void {
    this.setupAccountForm.valueChanges.subscribe(() => {
      const secretkeylenth = String(
        this.setupAccountForm.controls['password'].value,
      );
      if (secretkeylenth.length === 0) {
        this.passwordMeterLow = ' ';
        this.passwordMeterHigh = ' ';
        this.passwordMeterValue = ' ';
        this.passwordMeterOptimum = ' ';
        this.meterStatus = ' ';
      } else if (this.setupAccountForm.controls['password'].errors) {
        this.passwordMeterLow = '.25';
        this.passwordMeterHigh = '.75';
        this.passwordMeterValue = '.2';
        this.passwordMeterOptimum = '.8';
        this.meterStatus = 'Weak';
      } else if (secretkeylenth.length === 8) {
        this.passwordMeterLow = '.25';
        this.passwordMeterHigh = '.75';
        this.passwordMeterValue = '.5';
        this.passwordMeterOptimum = '.15';
        this.meterStatus = 'Fair';
      } else if (secretkeylenth.length > 8 && secretkeylenth.length <= 12) {
        this.passwordMeterLow = '.10';
        this.passwordMeterHigh = '1';
        this.passwordMeterValue = '.7';
        this.passwordMeterOptimum = '.15';
        this.meterStatus = 'Good';
      } else if (secretkeylenth.length > 12) {
        this.passwordMeterLow = '.10';
        this.passwordMeterHigh = '1';
        this.passwordMeterValue = '1';
        this.passwordMeterOptimum = '.20';
        this.meterStatus = 'Strong';
      }
    });
  }
}
