import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
@Injectable({providedIn: 'root'})
export class SearchTermService {
  private readonly searchParameter = new BehaviorSubject<string>('');
  searchParameter$: Observable<string>;
  constructor() {
    this.searchParameter$ = this.searchParameter.asObservable();
  }
  setSearchTerm(searchTerm: string): void {
    this.searchParameter.next(searchTerm);
  }
}
