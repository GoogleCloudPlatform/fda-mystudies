import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
@Injectable({providedIn: 'root'})
export class StateService {
  private readonly currentUserNameStore = new BehaviorSubject<string>('');
  currentUserName$: Observable<string>;
  constructor() {
    this.currentUserName$ = this.currentUserNameStore.asObservable();
  }
  setCurrentUserName(userName: string): void {
    this.currentUserNameStore.next(userName);
  }
}
