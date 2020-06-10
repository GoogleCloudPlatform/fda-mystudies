import {Injectable} from '@angular/core';
@Injectable()
export class AuthService {
  authUserID: string | undefined;
  ngOnInit() {}
  getAuthorizationToken(): string | undefined {
    return JSON.parse(localStorage.getItem('currentUser')).authToken;
  }

  getUserId(): string {
    return JSON.parse(localStorage.getItem('currentUser')).userID;
  }

  getAuthUserId(): string {
    return JSON.parse(localStorage.getItem('currentUser')).authUserID;
  }
}
