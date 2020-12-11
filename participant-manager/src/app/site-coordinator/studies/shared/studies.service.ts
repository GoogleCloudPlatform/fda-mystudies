import {Injectable} from '@angular/core';
import {EntityService} from 'src/app/service/entity.service';
import {Observable} from 'rxjs';
import {StudyResponse} from '../shared/study.model';
@Injectable({
  providedIn: 'root',
})
export class StudiesService {
  constructor(private readonly entityService: EntityService<StudyResponse>) {}
  getStudies(): Observable<StudyResponse> {
    return this.entityService.get('studies');
  }
  // getStudiesWithSites(): Observable<StudyResponse> {
  //   return this.entityService.get('sites');
  // }

  getStudiesWithSites(
    limit: number,
    offset: number,
    searchTerm: string,
  ): Observable<StudyResponse> {
    return this.entityService.get(
      // eslint-disable-next-line @typescript-eslint/restrict-plus-operands
      'sites?limit=' +
        limit +
        '&offset=' +
        offset +
        '&searchTerm=' +
        searchTerm,
    );
  }
}
