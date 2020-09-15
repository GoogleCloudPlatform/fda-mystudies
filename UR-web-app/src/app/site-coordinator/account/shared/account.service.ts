import {Injectable} from '@angular/core';
import {Profile, UpdateProfile} from './profile.model';
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
    authUserId='';
  constructor(
    private readonly entityService: EntityService<Profile>,
    private readonly http: HttpClient,
    private readonly authService: AuthService,
  ) {
  this.authUserId= this.authService.getAuthUserId();
  }

  fetchProfile(): Observable<Profile> {
    return this.entityService.get(`/users/${encodeURIComponent( this.authUserId)}`);
  }
  updateUserProfile(
    profileToBeUpdated: UpdateProfile,
  ): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(
      `${environment.baseUrl}/users/${encodeURIComponent(
        this.authUserId,
      )}/profile`,
      profileToBeUpdated,
    );
  }

  logout(): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${environment.authServerUrl}/users/${encodeURIComponent(
         this.authUserId,
      )}/logout`,
      '',
    );
  }
}
