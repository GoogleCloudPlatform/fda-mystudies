import {SubscriptionLike} from 'rxjs';
export class SubSink {
  protected subs: SubscriptionLike[] = [];
  add(...subscriptions: SubscriptionLike[]): void {
    this.subs = this.subs.concat(subscriptions);
  }
  unsubscribe(): void {
    this.subs.forEach((sub) => sub.unsubscribe());
    this.subs = [];
  }
}
