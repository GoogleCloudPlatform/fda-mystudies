import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
@Injectable({providedIn: 'root'})
export class HeaderDisplayService {
  private readonly showHeader = new BehaviorSubject<boolean>(true);
  showHeaders$: Observable<boolean>;
  constructor() {
    this.showHeaders$ = this.showHeader.asObservable();
  }
  setDisplayHeaderStatus(changeHeaderDisplayStatus: boolean): void {
    this.showHeader.next(changeHeaderDisplayStatus);
  }
}
