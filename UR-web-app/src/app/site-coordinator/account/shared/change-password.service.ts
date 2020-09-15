import {Injectable} from '@angular/core';
import {ChangePassword} from './profile.model';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {environment} from '@environment';
import {AuthService} from '../../../service/auth.service';
@Injectable({
  providedIn: 'root',
})
export class ChangePasswordService {
  userId = '';
  constructor(
    private readonly http: HttpClient,
    private readonly authService: AuthService,
  ) {
    this.userId = this.authService.getAuthUserId();
  }

  changePassword(changePassword: ChangePassword): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(
      `${environment.authServerUrl}/users/${encodeURIComponent(
        this.userId,
      )}/change_password`,
      changePassword,
    );
  }
}
