import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Validators, FormBuilder, FormGroup} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {ChangePasswordService} from '../shared/change-password.service';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {getMessage} from 'src/app/shared/success.codes.enum';
import {ChangePassword} from '../shared/profile.model';
import {mustMatch, passwordValidator} from 'src/app/_helper/validator';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss'],
})
export class ChangePasswordComponent extends UnsubscribeOnDestroyAdapter
  implements OnInit {
  resetPasswordForm: FormGroup;
  constructor(
    private readonly fb: FormBuilder,
    private readonly cahangePasswordService: ChangePasswordService,
    private readonly route: ActivatedRoute,
    private readonly toastr: ToastrService,
  ) {
    super();
    this.resetPasswordForm = this.fb.group(
      {
        currentPassword: ['', Validators.required],
        newPassword: ['', [Validators.required, passwordValidator()]],
        confirmPassword: ['', [Validators.required]],
      },
      {
        validator: [mustMatch('newPassword', 'confirmPassword')],
      },
    );
  }
  passCriteria = '';
  // eslint-disable-next-line no-invalid-this

  get ressetPassword() {
    return this.resetPasswordForm.controls;
  }
  ngOnInit(): void {
    this.passCriteria = `Your password must be 8 to 64 characters long.  
                        - contain a lower case letter.
                        - contain an upper case letter. 
                        - contain a number.
                        - contain a special character from the following set:
                        !"" # $ % ' () * + , - . : ; < = > ? @ [] ^_  {} |~"' `;
  }
  changePassword(): void {
    if (!this.resetPasswordForm.valid) return;

    const changePassword: ChangePassword = {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      currentPassword: String(this.resetPasswordForm.value.currentPassword),

      // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
      newPassword: String(this.resetPasswordForm.value.newPassword),
    };

    this.cahangePasswordService.changePassword(changePassword).subscribe(
      (successResponse: ApiResponse) => {
        this.toastr.success(successResponse.message);
      },
      (errorResponse: ApiResponse) => {
        if (getMessage(errorResponse.code)) {
          this.toastr.success(getMessage(errorResponse.code));
        }
      },
    );
  }
}
