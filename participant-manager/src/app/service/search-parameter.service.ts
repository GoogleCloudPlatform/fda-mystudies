import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
@Injectable({providedIn: 'root'})
export class SearchParameterService {
  private readonly searchParameter = new BehaviorSubject<string>('');
  searchParam$: Observable<string>;
  constructor() {
    this.searchParam$ = this.searchParameter.asObservable();
  }
  setSearchParameter(parameter: string): void {
    this.searchParameter.next(parameter);
  }
}
