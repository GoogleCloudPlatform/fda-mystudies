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
import {mustMatch, passwordValidator} from 'src/app/_helper/validator';

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
        validator: [mustMatch('password', 'confirmPassword')],
      },
    );
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
  }

  getPreStoredDetails(): void {
    this.setUpAccountService.get(this.setUpCode).subscribe(
      (user) => {
        if (user.redirectTo === 'login') {
          void this.router.navigate(['/pagenotfound']);
        }
        this.setupAccountForm.patchValue(user);
      },
      (err) => {
        console.log(err);
        void this.router.navigate(['/login']);
      },
    );
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
}
