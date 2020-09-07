import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SharedService {
  searchPlaceHolder$ = new BehaviorSubject('');
  updatedSearchPlaceHolder: Observable<string>;

  constructor() {
    this.updatedSearchPlaceHolder = this.searchPlaceHolder$.asObservable();
  }

  updateSearchPlaceHolder(placeHolder: string): void {
    this.searchPlaceHolder$.next(placeHolder);
  }
}
