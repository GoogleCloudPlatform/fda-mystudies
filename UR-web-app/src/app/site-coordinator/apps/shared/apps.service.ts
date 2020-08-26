import {Injectable} from '@angular/core';
import {EntityService} from 'src/app/service/entity.service';
import {App} from './app.model';
import {Observable} from 'rxjs';
import {AppDetails} from '../../user/shared/app-details';
import {environment} from '@environment';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AppsService {
  constructor(
    private readonly entityService: EntityService<App>,
    private readonly http: HttpClient,
  ) {}

  getUserApps(): Observable<App[]> {
    return this.entityService.getCollection('apps');
  }

  getAllAppsWithStudiesAndSites(): Observable<AppDetails> {
    return this.http.get<AppDetails>(`${environment.baseUrl}/apps`, {
      params: {fields: 'studies,sites'},
    });
  }
}
