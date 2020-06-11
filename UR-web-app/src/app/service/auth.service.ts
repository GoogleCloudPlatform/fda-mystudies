import {Injectable} from '@angular/core';
import {User} from '../entity/User';
@Injectable()
export class AuthService {
  ngOnInit() {}

  getUserDetails():User {
    return JSON.parse(localStorage.getItem('currentUser')!);
  }
}
