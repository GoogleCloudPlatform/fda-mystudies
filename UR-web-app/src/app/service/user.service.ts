import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../entity/user';
import {Observable} from 'rxjs';
import {environment} from 'src/environments/environment';
@Injectable({providedIn: 'root'})
export class UserService {
  constructor(private readonly http: HttpClient) {}

  getUserDetails(): Observable<User> {
    return this.http.get<User>(`${environment.baseUrl}/users/profile`);
  }
}
