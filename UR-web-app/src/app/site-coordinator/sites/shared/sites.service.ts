import {Injectable} from '@angular/core';
import {EntityService} from 'src/app/service/entity.service';
import {Observable} from 'rxjs';
import {AddSite} from './add.sites.model';

@Injectable({
  providedIn: 'root',
})
export class SitesService {
  constructor(private readonly entityService: EntityService<AddSite>) {}

  add(addSite: AddSite): Observable<AddSite> {
    return this.entityService.post(JSON.stringify(addSite), 'sites');
  }
}
