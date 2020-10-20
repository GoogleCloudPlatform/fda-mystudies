import {Injectable} from '@angular/core';
import {environment} from '@environment';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Resetpassword} from './forgot-password';

@Injectable({
  providedIn: 'root',
})
export class ForgotPasswordService {
  constructor(private readonly http: HttpClient) {}

  resetPassword(resetData: Resetpassword): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${environment.authServerUrl}/user/reset_password`,
      resetData,
    );
  }
}
