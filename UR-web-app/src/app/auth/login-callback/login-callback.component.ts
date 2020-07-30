import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {CookieService} from 'ngx-cookie-service';
import {AuthService} from 'src/app/service/auth.service';

@Component({
  selector: 'login-callback',
  template: '',
})
export class LoginCallbackComponent implements OnInit {
  constructor(
    private readonly activatedRoute: ActivatedRoute,
    private readonly http: HttpClient,
    public cookieService: CookieService,
    public authService: AuthService,
    private readonly router: Router,
  ) {}
  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      if (params.code && params.userId) {
        this.authService.grantAuthorization(params.code, params.userId);
      }
    });
  }
}
