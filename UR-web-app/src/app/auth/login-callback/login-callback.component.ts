import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from 'src/app/service/auth.service';

@Component({
  selector: 'login-callback',
  template: '',
})
export class LoginCallbackComponent implements OnInit {
  constructor(
    private readonly activatedRoute: ActivatedRoute,
    public authService: AuthService,
    private readonly router: Router,
  ) {}
  ngOnInit(): void {
    this.redirect();
  }

  redirect(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      sessionStorage.setItem('code', params.code);
      sessionStorage.setItem('userId', params.userId);
      if (params.code && params.userId) {
        this.authService
          .grantAuthorization(params.code, params.userId)
          .subscribe(
            (res) => {
              sessionStorage.setItem('accessToken', res.access_token);
              sessionStorage.setItem('refreshToken', res.refresh_token);
              if (params.accountStatus === 3) {
                void this.router.navigate(['/change-password']);
              } else {
                void this.router.navigate(['/coordinator']);
              }
            },
            () => {
              void this.router.navigate(['/login']);
            },
          );
      }
    });
  }
}
