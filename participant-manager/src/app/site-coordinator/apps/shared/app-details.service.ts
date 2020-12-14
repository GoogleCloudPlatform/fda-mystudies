import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {AppDetails} from './app-details';
import {environment} from '@environment';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AppDetailsService {
  constructor(private readonly http: HttpClient) {}

  get(
    appId: string,
    offset: number,
    limit: number,
    searchTerm: string,
    sortBy: string,
    sortOrder: string,
  ): Observable<AppDetails> {
    return this.http.get<AppDetails>(
      `${environment.participantManagerDatastoreUrl}/apps/${encodeURIComponent(
        appId,
      )}/participants`,
      {
        params: {
          excludeParticipantStudyStatus: ['notEligible', 'yetToEnroll'],
          offset: offset.toString(),
          limit: limit.toString(),
          searchTerm: searchTerm,
          sortBy: sortBy,
          sortDirection: sortOrder,
        },
      },
    );
  }
}
