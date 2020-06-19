/* eslint-disable @typescript-eslint/explicit-module-boundary-types */
/* eslint-disable @typescript-eslint/promise-function-async */
import {defer} from 'rxjs';
export function asyncData<T>(data: T) {
  return defer(() => Promise.resolve(data));
}

export function asyncError<T>(errorObject: unknown) {
  return defer(() => Promise.reject(errorObject));
}
