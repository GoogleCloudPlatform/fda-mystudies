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
    return this.http.post<ApiResponse>(
      `${environment.participantManagerDatastoreUrl}/users`,
      user,
    );
  }

  update(user: User, adminId: string): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(
      `${environment.participantManagerDatastoreUrl}/users/${adminId}/`,
      user,
    );
  }

  updateStatus(
    updateStatusRequest: UpdateStatusRequest,
    adminId: string,
  ): Observable<ApiResponse> {
    return this.http.patch<ApiResponse>(
      `${environment.participantManagerDatastoreUrl}/users/${adminId}/`,
      updateStatusRequest,
    );
  }

  resendInvitation(adminId: string): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${environment.participantManagerDatastoreUrl}/users/${adminId}/invite`,
      {},
    );
  }

  getUserDetails(adminId: string): Observable<ManageUserDetails> {
    return this.http.get<ManageUserDetails>(
      `${environment.participantManagerDatastoreUrl}/users/admin/${adminId}`,
    );
  }

  getUserDetailsForEditing(adminId: string): Observable<ManageUserDetails> {
    return this.http.get<ManageUserDetails>(
      `${environment.participantManagerDatastoreUrl}/users/admin/${adminId}`,
      {
        params: {includeUnselected: 'true'},
      },
    );
  }

  deleteInvitation(adminId: string): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(
      `${environment.participantManagerDatastoreUrl}/users/${adminId}/`,
    );
  }
  getUsers(): Observable<ManageUsers> {
    return this.http.get<ManageUsers>(
      `${environment.participantManagerDatastoreUrl}/users`,
    );
  }
}
