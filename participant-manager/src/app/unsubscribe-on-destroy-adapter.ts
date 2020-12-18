import {OnDestroy} from '@angular/core';
import {SubSink} from './sub-sink';

/**
 * A class that automatically unsubscribes all observables when the object gets destroyed
 */
export class UnsubscribeOnDestroyAdapter implements OnDestroy {
  /**
   * The subscription sink object that stores all subscriptions
   */
  subs = new SubSink();

  /**
   * The lifecycle hook that unsubscribes all subscriptions when the component / object gets destroyed
   */
  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
