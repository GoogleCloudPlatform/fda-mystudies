import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
@Injectable({providedIn: 'root'})
export class StateService {
  currentUserNameStore = new BehaviorSubject<string>('');
  currentUserName$: Observable<string> = of('');
  constructor() {
    this.currentUserName$ = this.currentUserNameStore.asObservable();
  }
  setCurrentUserName(userName: string): void {
    this.currentUserNameStore.next(userName);
  }
}
