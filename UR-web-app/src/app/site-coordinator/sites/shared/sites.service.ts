import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {AddSiteRequest} from './add.sites.request';
import {HttpClient} from '@angular/common/http';
import {environment} from 'src/environments/environment.prod';
import {ApiResponse} from 'src/app/entity/api.response.model';

@Injectable({
  providedIn: 'root',
})
export class SitesService {
  constructor(private readonly http: HttpClient) {}

  add(addSite: AddSiteRequest): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${environment.baseUrl}/sites`, addSite);
  }
}
