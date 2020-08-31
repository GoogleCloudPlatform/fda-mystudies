import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '@environment';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {ManageUserDetails} from './manage-user-details';
import {User} from 'src/app/entity/user';
import {ManageUsers} from './manage-user';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private readonly http: HttpClient) {}

  add(user: User): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${environment.baseUrl}/users`, user);
  }

  getUserDetails(adminId: string): Observable<ManageUserDetails> {
    return this.http.get<ManageUserDetails>(
      `${environment.baseUrl}/users/admin/${adminId}`,
    );
  }

  getUsers(): Observable<ManageUsers> {
    return this.http.get<ManageUsers>(`${environment.baseUrl}/users`);
  }
}
