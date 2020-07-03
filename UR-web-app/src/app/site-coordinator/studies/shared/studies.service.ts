import {Injectable} from '@angular/core';
import {EntityService} from 'src/app/service/entity.service';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class StudiesService {
  constructor(private readonly entityService: EntityService<unknown>) {}
  getStudies(): Observable<unknown[]> {
    return this.entityService.getCollection('studies');
  }
}
