import {Injectable} from '@angular/core';
import {CanActivate, UrlTree, Router} from '@angular/router';
import {AuthService} from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
  ) {}

  canActivate(): boolean | UrlTree {
    if (!this.authService.hasCredentials()) {
      return this.router.parseUrl('/login');
    }
    return true;
  }
}
