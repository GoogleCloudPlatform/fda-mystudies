import {Injectable} from '@angular/core';
import {EntityService} from '../../../service/entity.service';
import {Observable} from 'rxjs';
import {Location} from '../shared/location.model';

@Injectable({
  providedIn: 'root',
})
export class LocationService {
  constructor(private readonly entityService: EntityService<unknown>) {}
  getLocations(): Observable<unknown[]> {
    return this.entityService.getCollection('locations');
  }
  addLocation(location: Location): Observable<unknown> {
    return this.entityService.post(JSON.stringify(location), 'locations');
  }
}
