import {Injectable} from '@angular/core';
import {CanActivate, UrlTree} from '@angular/router';
import {UserService} from '../service/user.service';
import {Profile} from '../site-coordinator/account/shared/profile.model';

@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate {
  user = {} as Profile;

  constructor(private readonly userService: UserService) {
    this.user = this.userService.getUserProfile();
  }

  canActivate(): boolean | UrlTree {
    return this.user.superAdmin;
  }
}
