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
    this.activatedRoute.queryParams.subscribe((params) => {
      if (params.code && params.userId) {
        this.authService.grantAuthorization(params.code, params.userId);
      } else {
        void this.router.navigate(['/login']);
      }
    });
  }
}
