import {Injectable} from '@angular/core';
import {EntityService} from 'src/app/service/entity.service';
import {ManageApps} from './app.model';
import {Observable} from 'rxjs';
import {AppDetails} from '../../user/shared/app-details';
import {environment} from '@environment';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AppsService {
  constructor(
    private readonly entityService: EntityService<ManageApps>,
    private readonly http: HttpClient,
  ) {}

  getUserApps(
    limit: number,
    offset: number,
    searchTerm: string,
  ): Observable<ManageApps> {
    return this.entityService.get(
      'apps?limit=' +
        limit.toString() +
        '&offset=' +
        offset.toString() +
        '&searchTerm=' +
        searchTerm,
    );
  }
  getAllAppsWithStudiesAndSites(): Observable<AppDetails> {
    return this.http.get<AppDetails>(
      `${environment.participantManagerDatastoreUrl}/apps`,
      {
        params: {fields: 'studies,sites'},
      },
    );
  }
}
