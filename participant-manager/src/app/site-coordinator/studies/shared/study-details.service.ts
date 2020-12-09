import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {StudyDetails} from './study-details';
import {EntityService} from 'src/app/service/entity.service';
import {UpdateTargetEnrollmentRequest} from './site.model';
import {ApiResponse} from 'src/app/entity/api.response.model';
import {HttpClient} from '@angular/common/http';
import {environment} from '@environment';

@Injectable({
  providedIn: 'root',
})
export class StudyDetailsService {
  constructor(
    private readonly entityService: EntityService<StudyDetails>,
    private readonly http: HttpClient,
  ) {}

  getStudyDetails(
    studyId: string,
    offset: number,
    limit: number,
    searchTerm:string,
    sortBy:string,
    sortOrder:string,
  ): Observable<StudyDetails> {
    console.log(offset);
    return this.http.get<StudyDetails>(
      `${
        environment.participantManagerDatastoreUrl
      }/studies/${encodeURIComponent(studyId)}/participants`,
      {
        params: {
          excludeParticipantStudyStatus: ['notEligible', 'yetToJoin'],
          offset: offset.toString(),
          limit: limit.toString(),
          searchTerm: searchTerm,
          sortBy: sortBy,
          sortDirection: sortOrder,
        },
      },
    );
  }
  updateTargetEnrollment(
    updateTargetEnrollment: UpdateTargetEnrollmentRequest,
    studyId: string,
  ): Observable<ApiResponse> {
    return this.http.patch<ApiResponse>(
      `${environment.participantManagerDatastoreUrl}/studies/${studyId}/targetEnrollment`,
      updateTargetEnrollment,
    );
  }
}
