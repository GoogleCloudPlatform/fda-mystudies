import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from 'src/app/service/auth.service';

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
    if (this.authService.getUserId() === '') {
      this.authService.initSessionStorage();
      setTimeout(() => {
        this.authService.beginLoginConsentFlow();
      }, 200);
    } else {
      void this.router.navigate(['/coordinator/studies/sites']);
    }
  }
}
