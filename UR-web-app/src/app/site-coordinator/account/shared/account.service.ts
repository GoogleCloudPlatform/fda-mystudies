import {Injectable} from '@angular/core';
import {ChangePassword, Profile} from './profile.model';
import {EntityService} from '../../../service/entity.service';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {environment} from '@environment';
import {AuthService} from '../../../service/auth.service';
@Injectable({
  providedIn: 'root',
})
export class AccountService {
  authUserId = '';
  constructor(
    private readonly entityService: EntityService<Profile>,
    private readonly http: HttpClient,
    private readonly authService: AuthService,
  ) {}

  fetchUserProfile(): Observable<Profile> {
    this.authUserId = this.authService.getAuthUserId();
    return this.entityService.get(
      `/users/${encodeURIComponent(this.authUserId)}`,
    );
  }

  changePassword(changePassword: ChangePassword): Observable<ApiResponse> {
    this.authUserId = this.authService.getAuthUserId();
    return this.http.put<ApiResponse>(
      `${environment.authServerUrl}/users/${this.authUserId}/change_password`,
      changePassword,
    );
  }
}
