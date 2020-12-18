import {Injectable} from '@angular/core';
import {EntityService} from 'src/app/service/entity.service';
import {User, SetUpUser} from 'src/app/entity/user';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '@environment';

import {SetUpResponse} from './set-up-account';

@Injectable({
  providedIn: 'root',
})
export class SetUpAccountService {
  constructor(
    private readonly entityService: EntityService<User>,
    private readonly http: HttpClient,
  ) {}
  get(setUpCode: string): Observable<User> {
    return this.entityService.get('users/securitycodes/' + setUpCode);
  }

  setUpAccount(userUpdate: SetUpUser): Observable<SetUpResponse> {
    return this.http.post<SetUpResponse>(
      `${environment.participantManagerDatastoreUrl}/users/setUpAccount/`,
      userUpdate,
    );
  }
}
