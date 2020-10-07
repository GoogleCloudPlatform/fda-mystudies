import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private readonly searchPlaceHolderSubject = new BehaviorSubject('');
  searchPlaceHolder$: Observable<string>;

  constructor() {
    this.searchPlaceHolder$ = this.searchPlaceHolderSubject.asObservable();
  }

  updateSearchPlaceHolder(placeHolder: string): void {
    this.searchPlaceHolderSubject.next(placeHolder);
  }
}
