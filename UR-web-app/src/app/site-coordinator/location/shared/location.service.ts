import {Injectable} from '@angular/core';
import {EntityService} from '../../../service/entity.service';
import {Observable} from 'rxjs';
import {
  Location,
  StatusUpdateRequest,
  FieldUpdateRequest,
} from '../shared/location.model';

@Injectable({
  providedIn: 'root',
})
export class LocationService {
  constructor(private readonly entityService: EntityService<Location>) {}
  getLocations(): Observable<Location[]> {
    return this.entityService.getCollection('locations');
  }
  addLocation(location: Location): Observable<Location> {
    return this.entityService.post(JSON.stringify(location), 'locations');
  }
  get(locationId: string): Observable<Location> {
    return this.entityService.get('locations/' + locationId);
  }
  update(
    locationToBeUpdated: StatusUpdateRequest | FieldUpdateRequest,
    locationId: string,
  ): Observable<Location> {
    return this.entityService.put(
      JSON.stringify(locationToBeUpdated),
      'locations/' + locationId,
    );
  }
}
