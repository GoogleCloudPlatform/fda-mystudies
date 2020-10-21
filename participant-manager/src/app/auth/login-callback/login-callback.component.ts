import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {switchMap} from 'rxjs/operators';
import {AccessToken} from 'src/app/entity/access-token';
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
    this.activatedRoute.queryParams.subscribe(
      (params) => {
        if (params.code && params.userId) {
          this.authService
            .getToken(params.code, params.userId)
            .pipe(
              switchMap((authServerResponse: AccessToken) => {
                sessionStorage.setItem('code', params.code);
                sessionStorage.setItem('authUserId', params.userId);
                sessionStorage.setItem(
                  'accessToken',
                  authServerResponse.access_token,
                );
                sessionStorage.setItem(
                  'refreshToken',
                  authServerResponse.refresh_token,
                );
                return this.accountService.fetchUserProfile();
              }),
            )
            .subscribe(
              (userProfile: Profile) => {
                this.userState.setCurrentUserName(userProfile.firstName);
                sessionStorage.setItem('userId', userProfile.userId);
                sessionStorage.setItem('user', JSON.stringify(userProfile));

                if (
                  params.accountStatus === '3' ||
                  params.accountStatus === '2'
                ) {
                  void this.router.navigate([
                    '/coordinator/accounts/change-password',
                  ]);
                } else {
                  void this.router.navigate(['/coordinator/studies/sites']);
                }
              },
              (error) => {
                this.toastr.error(error);
              },
            );
        }
      },
      () => {
        void this.router.navigate(['/login']);
      },
    );
  }
}
