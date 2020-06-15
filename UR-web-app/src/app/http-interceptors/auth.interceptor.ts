import {Injectable} from '@angular/core';
import {NgxSpinnerService} from 'ngx-spinner';
import {
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpEvent,
} from '@angular/common/http';
import {finalize} from 'rxjs/operators';
import {User} from '../entity/user';
import {Observable} from 'rxjs';
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private readonly spinner: NgxSpinnerService) {}

  getUserDetails(): User | null {
    const currentUser = localStorage.getItem('currentUser');
    if (currentUser === null) {
      return null;
    }
    return JSON.parse(currentUser) as User;
  }
  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler,
  ): Observable<HttpEvent<unknown>> {
    void this.spinner.show();
    const user = this.getUserDetails();
    if (user === null) {
      return next.handle(req).pipe(
        finalize(() => {
          void this.spinner.hide();
        }),
      );
    }
    const headers = req.headers
      .set('Content-Type', 'application/json')
      .set('userId', user.id.toString())
      .set('authToken', user.authToken)
      .set('authUserId', user.urAdminAuthId);
    const authReq = req.clone({headers});
    return next.handle(authReq).pipe(
      finalize(() => {
        void this.spinner.hide();
      }),
    );
  }
}
