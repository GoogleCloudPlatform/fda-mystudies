import {SubscriptionLike} from 'rxjs';
export class SubSink {
  protected _subs: SubscriptionLike[] = [];
  add(...subscriptions: SubscriptionLike[]): void {
    this._subs = this._subs.concat(subscriptions);
  }
  unsubscribe(): void {
    this._subs.forEach((sub) => sub.unsubscribe());
    this._subs = [];
  }
}
