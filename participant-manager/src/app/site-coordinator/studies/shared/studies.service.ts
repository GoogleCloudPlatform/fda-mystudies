import {Injectable} from '@angular/core';
import {EntityService} from 'src/app/service/entity.service';
import {Observable} from 'rxjs';
import {StudyResponse} from '../shared/study.model';
@Injectable({
  providedIn: 'root',
})
export class StudiesService {
  constructor(private readonly entityService: EntityService<StudyResponse>) {}
  getStudies(
    limit: number,
    offset: number,
    searchTerm: string,
  ): Observable<StudyResponse> {
    return this.entityService.get(
      'studies?limit=' +
        limit.toString() +
        '&offset=' +
        offset.toString() +
        '&searchTerm=' +
        searchTerm,
    );
  }

  getStudiesWithSites(
    limit: number,
    offset: number,
    searchTerm: string,
  ): Observable<StudyResponse> {
    return this.entityService.get(
      'sites?limit=' +
        limit.toString() +
        '&offset=' +
        offset.toString() +
        '&searchTerm=' +
        searchTerm,
    );
  }
}
