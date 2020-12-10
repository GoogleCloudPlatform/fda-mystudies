import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../entity/user';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
import {Profile} from '../site-coordinator/account/shared/profile.model';
@Injectable({providedIn: 'root'})
export class UserService {
  constructor(private readonly http: HttpClient) {}

  getUserDetails(): Observable<User> {
    return this.http.get<User>(
      `${environment.participantManagerDatastoreUrl}/users/profile`,
    );
  }

  getUserProfile(): Profile {
    const userObject = sessionStorage.getItem('user');
    let user = {} as Profile;
    if (userObject) user = JSON.parse(userObject) as Profile;
    return user;
  }
}
