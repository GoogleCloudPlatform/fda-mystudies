import {Component} from '@angular/core';
import {AuthService} from 'src/app/service/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  constructor(private readonly authService: AuthService) {}

  ngOnInit(): void {
    this.authService.initlocalStorage();
    setTimeout(() => {
      this.authService.beginLoginConsentFlow();
    }, 1000);
  }
}
