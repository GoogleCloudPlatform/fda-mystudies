import {Injectable} from '@angular/core';
import {User} from '../entity/User';
@Injectable()
export class AuthService {
  authUserID: string | undefined;
  user: User;
  userId:string;
  authUserId:string;
  authToken:string;
  ngOnInit() {}

  getAuthorizationToken(): string | undefined {
    this.authToken = '';
    this.user=JSON.parse(window.localStorage.getItem('currentUser'));
    if (this.user && this.user != null &&
      this.user != undefined &&
      Object.keys(this.user).length > 0) {
      this.authToken = this.user.authToken.toString();
    }
    return this.authToken;
  }

  getUserId(): string {
    this.userId = '';
    this.user=JSON.parse(window.localStorage.getItem('currentUser'));
    if (this.user && this.user != null &&
      this.user != undefined &&
      Object.keys(this.user).length > 0) {
      this.userId = this.user.id.toString();
    }

    return this.userId;
  }

  getAuthUserId(): string {
    this.authUserId = '';
    this.user=JSON.parse(window.localStorage.getItem('currentUser'));
    if (this.user && this.user != null &&
      this.user != undefined &&
      Object.keys(this.user).length > 0) {
      this.authUserId = this.user.urAdminAuthId.toString();
    }
    return this.authUserId;
  }
}
