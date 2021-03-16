import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {AddSiteRequest} from './add.sites.request';
import {HttpClient} from '@angular/common/http';
import {environment} from '@environment';
import {SiteResponse} from '../../studies/shared/site.model';

@Injectable({
  providedIn: 'root',
})
export class SitesService {
  constructor(private readonly http: HttpClient) {}

  add(addSite: AddSiteRequest): Observable<SiteResponse> {
    return this.http.post<SiteResponse>(
      `${environment.participantManagerDatastoreUrl}/sites`,
      addSite,
    );
  }
}
