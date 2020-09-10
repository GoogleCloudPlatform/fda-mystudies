import {Component} from '@angular/core';
import {AuthService} from 'src/app/service/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    this.redirectToAuth();
  }

  redirectToAuth(): void {
    if (this.authService.getUserAccessToken() === '') {
      this.authService.storeDefaultsValues();
      this.authService.grantAutoSignIn();
    } else {
      void this.router.navigate(['/login/']);
    }
  }
}
