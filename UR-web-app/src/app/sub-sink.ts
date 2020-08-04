import {SubscriptionLike} from 'rxjs';
export class SubSink {
  protected subs: SubscriptionLike[] = [];
  add(...subscriptions: SubscriptionLike[]): void {
    this.subs = this.subs.concat(subscriptions);
  }
  set sink(subscription: SubscriptionLike) {
    this.subs.push(subscription);
  }
  unsubscribe(): void {
    this.subs.forEach((sub) => sub.unsubscribe());
    this.subs = [];
  }
}
