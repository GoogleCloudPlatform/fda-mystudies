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

  getUserApps(limit: number, offset: number): Observable<ManageApps> {
    // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
    return this.entityService.get('apps?limit=' + limit + '&offset=' + offset);
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
