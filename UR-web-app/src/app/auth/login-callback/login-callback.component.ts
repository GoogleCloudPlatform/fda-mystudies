import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {AuthService} from 'src/app/service/auth.service';
import {StateService} from 'src/app/service/state.service';
import {AccountService} from 'src/app/site-coordinator/account/shared/account.service';
import {Profile} from 'src/app/site-coordinator/account/shared/profile.model';

@Component({
  selector: 'login-callback',
  template: '',
})
export class LoginCallbackComponent implements OnInit {
  constructor(
    private readonly activatedRoute: ActivatedRoute,
    public authService: AuthService,
    private readonly router: Router,
    private readonly accountService: AccountService,
    private readonly userState: StateService,
    private readonly toastr: ToastrService,
  ) {}
  ngOnInit(): void {
    this.redirect();
  }

  redirect(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      sessionStorage.setItem('code', params.code);
      sessionStorage.setItem('authUserId', params.userId);
      if (params.code && params.userId) {
        this.authService.getToken(params.code, params.userId).subscribe(
          (res) => {
            sessionStorage.setItem('accessToken', res.access_token);
            sessionStorage.setItem('refreshToken', res.refresh_token);
            this.accountService.fetchProfile().subscribe(
              (data: Profile) => {
                this.userState.setCurrentUserName(data.firstName);
                sessionStorage.setItem('userId', data.userId);
                if (params.accountStatus === 3) {
                  void this.router.navigate(['/change-password']);
                } else {
                  void this.router.navigate(['/coordinator']);
                }
              },
              (error) => {
                this.toastr.error(error);
              },
            );
          },
          () => {
            void this.router.navigate(['/login']);
          },
        );
      }
    });
  }
}
