import {Injectable} from '@angular/core';
import {Profile} from './profile.model';
import {EntityService} from '../../../service/entity.service';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

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

  fetchProfile(): Observable<Profile> {
    this.authUserId = this.authService.getAuthUserId();
    return this.entityService.get(
      `/users/${encodeURIComponent(this.authUserId)}`,
    );
  }
}
