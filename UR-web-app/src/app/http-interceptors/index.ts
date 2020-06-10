import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {NoopInterceptor} from './noop-interceptor';
import {AuthInterceptor} from './Auth-Interceptor';

export const httpInterceptorProviders = [
  {provide: HTTP_INTERCEPTORS, useClass: NoopInterceptor, multi: true},
  {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},

];
