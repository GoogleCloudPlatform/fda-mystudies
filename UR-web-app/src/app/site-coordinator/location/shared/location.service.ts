import {Injectable} from '@angular/core';
import {EntityService} from '../../../service/entity.service';
import {Observable} from 'rxjs';
import {Location} from '../shared/location.model';
import {ApiSuccessResponse} from 'src/app/entity/sucess.model';

@Injectable({
  providedIn: 'root',
})
export class LocationService {
  constructor(private readonly entityService: EntityService<Location>) {}
  getLocations(): Observable<Location[]> {
    return this.entityService.getCollection('locations');
  }
  addLocation(location: Location): Observable<ApiSuccessResponse> {
    return this.entityService.post(JSON.stringify(location), 'locations');
  }
}
