import {Injectable} from '@angular/core';
import {EntityService} from 'src/app/service/entity.service';
import {Observable} from 'rxjs';
import {Study} from '../shared/study.model';
@Injectable({
  providedIn: 'root',
})
export class StudiesService {
  constructor(private readonly entityService: EntityService<Study>) {}
  getStudies(): Observable<Study[]> {
    return this.entityService.getCollection('studies');
  }
  getStudiesWithSites(): Observable<Study[]> {
    return this.entityService.getCollection('sites');
  }
}
