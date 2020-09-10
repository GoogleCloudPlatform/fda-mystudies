import {Injectable} from '@angular/core';
import {Profile, UpdateProfile} from './profile.model';
import {EntityService} from '../../../service/entity.service';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {environment} from '@environment';
import {User} from '../../../entity/user';
import {AuthService} from '../../../service/auth.service';
@Injectable({
  providedIn: 'root',
})
export class AccountService {
  user: User;
  constructor(
    private readonly entityService: EntityService<Profile>,
    private readonly http: HttpClient,
    private readonly authService: AuthService,
  ) {
    this.user = this.authService.getUser();
  }

  fetchProfile(): Observable<Profile> {
    return this.entityService.get(`/users/${encodeURIComponent(this.user.id)}`);
  }
  updateUserProfile(
    profileToBeUpdated: UpdateProfile,
  ): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(
      `${environment.baseUrl}/users/${encodeURIComponent(
        this.user.id,
      )}/profile`,
      profileToBeUpdated,
    );
  }

  logout(): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${environment.authServerUrl}/users/${encodeURIComponent(
        this.user.id,
      )}/logout`,
      '',
    );
  }
}
