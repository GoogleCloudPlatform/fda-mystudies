import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AppDetails} from './app-details';
import {Observable} from 'rxjs';
import {environment} from '@environment';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {User} from 'src/app/entity/user';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private readonly http: HttpClient) {}
  getAllApps(): Observable<AppDetails> {
    return this.http.get<AppDetails>(
      `${environment.baseUrl}/apps?fields=studies,sites`,
    );
  }
  add(user: User): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${environment.baseUrl}/users`, user);
  }
}
