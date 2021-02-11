import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Validators, FormBuilder, FormGroup} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {AccountService} from '../shared/account.service';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {ChangePassword} from '../shared/profile.model';
import {mustMatch, passwordValidator} from 'src/app/_helper/validator';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {HeaderDisplayService} from 'src/app/service/header-display.service';
@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss'],
})
export class ChangePasswordComponent
  extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  resetPasswordForm: FormGroup;
  changePasswordTitle = 'Change password';
  currentPasswordValidationMessage = 'Enter your current password';
  currentPasswordPlaceholder = 'Enter current password';
  currentPasswordlabel = 'Current password';
  hideClickable = true;
  passwordMeterLow = '.25';
  passwordMeterHigh = '.75';
  passwordMeterValue = '.2';
  passwordMeterOptimum = '.8';
  meterStatus = '';
  submitted = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly accountService: AccountService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly toastr: ToastrService,
    private readonly displayHeader: HeaderDisplayService,
  ) {
    super();
    this.resetPasswordForm = this.fb.group(
      {
        // eslint-disable-next-line @typescript-eslint/unbound-method
        currentPassword: ['', [Validators.required]],
        // eslint-disable-next-line @typescript-eslint/unbound-method
        newPassword: ['', [Validators.required, passwordValidator()]],
        // eslint-disable-next-line @typescript-eslint/unbound-method
        confirmPassword: ['', [Validators.required]],
      },
      {
        validator: [mustMatch('newPassword', 'confirmPassword')],
      },
    );
    this.onChange();
  }
  passCriteria = '';
  get ressetPassword() {
    return this.resetPasswordForm.controls;
  }
  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      if (params.action && params.action === 'passwordsetup') {
        this.changePasswordTitle = 'Set up password';
        this.currentPasswordValidationMessage = 'Enter your temporary password';
        this.currentPasswordPlaceholder = 'Enter temporary password';
        this.currentPasswordlabel = 'Temporary password';
      }
    }),
      (this.passCriteria = `Your password must be at least 8 characters long    
and contain lower case, upper case, numeric and
special characters.`);
    this.displayHeader.showHeaders$.subscribe((visible) => {
      this.hideClickable = visible;
    });
  }
  changePassword(): void {
    if (!this.resetPasswordForm.valid) return;
    this.submitted = true;

    const changePassword: ChangePassword = {
      currentPassword: String(
        this.resetPasswordForm.controls['currentPassword'].value,
      ),
      newPassword: String(this.resetPasswordForm.controls['newPassword'].value),
    };
    this.accountService
      .changePassword(changePassword)
      .subscribe((successResponse: ApiResponse) => {
        this.displayHeader.setDisplayHeaderStatus(true);
        if (getMessage(successResponse.code)) {
          this.toastr.success(getMessage(successResponse.code));
        }
        void this.router.navigate(['/coordinator/studies/sites']);
      });
  }
  cancel(): void {
    void this.router.navigate(['/coordinator/studies/sites']);
  }
  onChange(): void {
    this.resetPasswordForm.valueChanges.subscribe(() => {
      // if (typeof val.phoneNumber === 'string') {
      //   // const maskedVal = this.currencyMask.transform(val.amount);
      //   // if (val.amount ) {
      //   //   this.registerForm.patchValue({amount: maskedVal});
      //   // }
      // }
      const secretkeylenth = String(
        this.resetPasswordForm.controls['newPassword'].value,
      );
      if (this.resetPasswordForm.controls['newPassword'].errors) {
        this.passwordMeterLow = '.25';
        this.passwordMeterHigh = '.75';
        this.passwordMeterValue = '.2';
        this.passwordMeterOptimum = '.8';
        this.meterStatus = 'Weak';
      } else if (secretkeylenth.length >= 8 && secretkeylenth.length <= 16) {
        this.passwordMeterLow = '.25';
        this.passwordMeterHigh = '.75';
        this.passwordMeterValue = '.5';
        this.passwordMeterOptimum = '.15';
        this.meterStatus = 'Fair';
      } else if (secretkeylenth.length > 16 && secretkeylenth.length <= 32) {
        this.passwordMeterLow = '.10';
        this.passwordMeterHigh = '1';
        this.passwordMeterValue = '.7';
        this.passwordMeterOptimum = '.15';
        this.meterStatus = 'Good';
      } else if (secretkeylenth.length > 32) {
        this.passwordMeterLow = '.10';
        this.passwordMeterHigh = '1';
        this.passwordMeterValue = '1';
        this.passwordMeterOptimum = '.20';
        this.meterStatus = 'Strong';
      }
    });
  }
}
