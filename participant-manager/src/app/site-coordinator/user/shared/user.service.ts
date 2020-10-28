import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '@environment';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {ManageUserDetails, UpdateStatusRequest} from './manage-user-details';
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

  update(user: User, adminId: string): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(
      `${environment.baseUrl}/users/${adminId}/`,
      user,
    );
  }

  updateStatus(
    updateStatusRequest: UpdateStatusRequest,
    adminId: string,
  ): Observable<ApiResponse> {
    return this.http.patch<ApiResponse>(
      `${environment.baseUrl}/users/${adminId}/`,
      updateStatusRequest,
    );
  }

  resendInvitation(adminId: string): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${environment.baseUrl}/users/${adminId}/invite`,
      {},
    );
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
