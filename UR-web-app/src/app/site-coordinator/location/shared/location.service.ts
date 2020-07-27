import {Injectable} from '@angular/core';
import {EntityService} from '../../../service/entity.service';
import {Observable} from 'rxjs';
import {Location} from '../shared/location.model';

@Injectable({
  providedIn: 'root',
})
export class LocationService {
  constructor(private readonly entityService: EntityService<Location>) {}
  getLocations(): Observable<Location[]> {
    return this.entityService.getCollection('locations');
  }
  getLocationsForSiteCreation(studyId: string): Observable<Location[]> {
    return this.entityService.getCollection(
      'locations-for-site-creation?studyId=' + studyId,
    );
  }
  addLocation(location: Location): Observable<Location> {
    return this.entityService.post(JSON.stringify(location), 'locations');
  }
}
