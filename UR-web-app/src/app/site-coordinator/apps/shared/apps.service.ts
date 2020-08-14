import {Injectable} from '@angular/core';
import {EntityService} from 'src/app/service/entity.service';
import {App} from './app.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AppsService {
  constructor(private readonly entityService: EntityService<App>) {}
  getApps(): Observable<App[]> {
    return this.entityService.getCollection('apps');
  }
}
