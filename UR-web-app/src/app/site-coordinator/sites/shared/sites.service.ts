import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {AddSite} from './add.sites.model';
import {HttpClient} from '@angular/common/http';
import {environment} from 'src/environments/environment.prod';

@Injectable({
  providedIn: 'root',
})
export class SitesService {
  constructor(private readonly http: HttpClient) {}

  add(addSite: AddSite): Observable<AddSite> {
    return this.http.post<AddSite>(`${environment.baseUrl}/sites`, addSite);
  }
}
