import {Component, OnInit, ViewChild} from '@angular/core';
import {NgForm} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {SetUpUser} from '../../entity/user';
import {UnsubscribeOnDestroyAdapter} from 'src/app/unsubscribe-on-destroy-adapter';
import {SetUpAccountService} from 'src/app/auth/shared/set-up-account.service';
import {getMessage, getSuccessMessage} from 'src/app/shared/success.codes.enum';
import {AuthService} from 'src/app/service/auth.service';
import {SetUpResponse} from '../shared/set-up-account';

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
  @ViewChild('setupaccount')
  form!: NgForm;

  constructor(
    private readonly setUpAccountService: SetUpAccountService,
    private readonly authService: AuthService,
    private readonly route: ActivatedRoute,
    private readonly toastr: ToastrService,
  ) {
    super();
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
    this.setUpAccountService.get(this.setUpCode).subscribe((user) => {
      this.user = user;
    });
  }

  registerUser(): void {
    this.subs.add(
      this.setUpAccountService
        .setUpAccount(this.form.value)
        .subscribe((successResponse: SetUpResponse) => {
          this.toastr.success(
            getSuccessMessage(successResponse.code, successResponse.message),
          );
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
