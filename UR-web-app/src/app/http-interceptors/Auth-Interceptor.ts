import {AuthService} from '../service/auth.service';
import {Injectable} from '@angular/core';
import {NgxSpinnerService} from 'ngx-spinner';
import {
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
} from '@angular/common/http';
import {finalize} from 'rxjs/operators';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService, private spinner: NgxSpinnerService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    this.spinner.show();
    const userId = this.auth.getUserId();
    const authUserId = this.auth.getAuthUserId();
    const authToken = this.auth.getAuthorizationToken();
    if (userId != null && authUserId != null && authToken != null) {
      const headers = req.headers
          .set('Content-Type', 'application/json')
          .set('userId', userId)
          .set('authToken', authToken)
          .set('authUserId', authUserId);
      const authReq = req.clone({headers});
      return next.handle(authReq).pipe(
          finalize(() => {
            this.spinner.hide();
          }),
      );
    }
    return next.handle(req);
  }
}
