import {Injectable} from '@angular/core';
import {EntityService} from '../../../service/entity.service';
import {Observable} from 'rxjs';
import {
  Location,
  StatusUpdateRequest,
  FieldUpdateRequest,
  UpdateLocationResponse,
  ManageLocations,
} from '../shared/location.model';
import {HttpClient} from '@angular/common/http';
import {environment} from '@environment';

@Injectable({
  providedIn: 'root',
})
export class LocationService {
  constructor(
    private readonly entityService: EntityService<Location>,
    private readonly http: HttpClient,
  ) {}
  getLocations(
    offset: number,
    limit: number,
    searchTerm: string,
    sortBy: string,
    sortOrder: string,
  ): Observable<ManageLocations> {
    return this.http.get<ManageLocations>(
      `${environment.participantManagerDatastoreUrl}/locations`,
      {
        params: {
          offset: offset.toString(),
          limit: limit.toString(),
          searchTerm: searchTerm,
          sortBy: sortBy,
          sortDirection: sortOrder,
        },
      },
    );
  }
  getLocationsForSiteCreation(studyId: string): Observable<ManageLocations> {
    return this.http.get<ManageLocations>(
      `${environment.participantManagerDatastoreUrl}/locations`,
      {
        params: {excludeStudyId: studyId, status: '1'},
      },
    );
  }
  addLocation(location: Location): Observable<Location> {
    return this.entityService.post(location, 'locations');
  }
  get(locationId: string): Observable<Location> {
    return this.entityService.get('locations/' + locationId);
  }
  update(
    locationToBeUpdated: StatusUpdateRequest | FieldUpdateRequest,
    locationId: string,
  ): Observable<UpdateLocationResponse> {
    return this.http.put<UpdateLocationResponse>(
      `${environment.participantManagerDatastoreUrl}/locations/${locationId}`,
      locationToBeUpdated,
    );
  }
}
