import {Component} from '@angular/core';
import {FormGroup, FormBuilder} from '@angular/forms';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {ForgotPasswordService} from 'src/app/auth/shared/forgot-password.service';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {ToastrService} from 'ngx-toastr';
import {Router} from '@angular/router';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {CookieService} from 'ngx-cookie-service';
import { AuthService } from 'src/app/service/auth.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss'],
})
export class ForgotPasswordComponent extends UnsubscribeOnDestroyAdapter {
  forgotPasswordForm!: FormGroup;

  constructor(
    fb: FormBuilder,
    private readonly forgotPasswordService: ForgotPasswordService,
    private readonly toastr: ToastrService,
    private readonly router: Router,
    public cookieService: CookieService,
        private readonly authService: AuthService,

  ) {
    super();
    this.forgotPasswordForm = fb.group({
      email: '',
      appId: this.authService.appId,

    });
  }

  get getForgotPasswordForm() {
    return this.forgotPasswordForm.controls;
  }

  forgotPassword() {
    this.subs.add(
      this.forgotPasswordService
        .resetPassword(this.forgotPasswordForm.value)
        .subscribe((successResponse: ApiResponse) => {
          if (getMessage(successResponse.code)) {
            this.toastr.success(getMessage(successResponse.code));
          } else {
            this.toastr.success('sucess');
          }
           void this.router.navigate(['/login']);
        }),
    );
  }
}
