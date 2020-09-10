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
  constructor(
    private readonly entityService: EntityService<Profile>,
    private readonly http: HttpClient,
    private readonly authService: AuthService,
  ) {}
  // eslint-disable-next-line no-invalid-this
  id = this.authService.getAuthUserId();
  fetchProfile(): Observable<Profile> {
    return this.entityService.get(`/users/${encodeURIComponent(this.id)}`);
  }
  updateUserProfile(
    profileToBeUpdated: UpdateProfile,
  ): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(
      `${environment.baseUrl}/users/${encodeURIComponent(this.id)}/profile`,
      profileToBeUpdated,
    );
  }
}
