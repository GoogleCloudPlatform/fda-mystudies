import {Injectable} from '@angular/core';
import {NgxSpinnerService} from 'ngx-spinner';
import {
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpEvent,
  HttpErrorResponse,
  HttpResponse,
} from '@angular/common/http';
import {finalize} from 'rxjs/operators';
import {User} from '../entity/user';
import {Observable, OperatorFunction, throwError, of} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ToastrService} from 'ngx-toastr';
import {getMessage} from '../shared/error.codes.enum';
import {AuthService} from '../service/auth.service';
import accessToken from 'src/app/auth/access-token.json';
import {AccessToken} from '../entity/access-token';
import account from 'src/app/auth/account.json';
import {ApiResponse} from '../entity/api.response.model';
import {environment} from 'src/environments/environment';
import {CookieService} from 'ngx-cookie-service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private readonly spinner: NgxSpinnerService,
    private readonly toasterService: ToastrService,
    private readonly authService: AuthService,
    public cookieService: CookieService,
  ) {}

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler,
  ): Observable<HttpEvent<unknown>> {
    if (req.url === `${environment.authServerUrl}/oauth2/token`) {
      return of(
        new HttpResponse({
          status: 200,
          body: accessToken as AccessToken,
        }),
      );
    }
    if (req.url === `${environment.baseUrl}/users`) {
      return of(
        new HttpResponse({
          status: 200,
          body: account as User,
        }),
      );
    }
    void this.spinner.show();

    if (!this.authService.hasCredentials()) {
      return next.handle(req).pipe(
        this.handleError(),
        finalize(() => {
          void this.spinner.hide();
        }),
      );
    }
    const user: User = this.authService.getUser();
    if (req.url.includes(`${environment.authServerUrl}`)) {
      const headers = req.headers
        .set('Content-Type', 'application/x-www-form-urlencoded')
        .set('Accept', 'application/json')
        .set('correlationId', this.cookieService.get('correlationId'))
        .set('appId', 'PARTICIPANT-MANAGER')
        .set('mobilePlatform', 'DESKTOP');
      const authReq = req.clone({headers});
      return next.handle(authReq).pipe(
        this.handleError(),
        finalize(() => {
          void this.spinner.hide();
        }),
      );
    } else {
      const headers = req.headers
        .set('Content-Type', 'application/json')
        .set('userId', user.id.toString())
        .set('authToken', this.authService.getUserAccessToken())
        .set('authUserId', user.urAdminAuthId);
      const authReq = req.clone({headers});
      return next.handle(authReq).pipe(
        this.handleError(),
        finalize(() => {
          void this.spinner.hide();
        }),
      );
    }
  }
  handleError<T>(): OperatorFunction<T, T> {
    return catchError(
      (err: unknown): Observable<T> => {
        if (err instanceof HttpErrorResponse) {
          if (err.error instanceof ErrorEvent) {
            this.toasterService.error(err.error.message);
          } else {
            const customError = err.error as ApiResponse;
            if (getMessage(customError.error_code)) {
              this.toasterService.error(getMessage(customError.error_code));
            } else {
              this.toasterService.error(
                `Error Code: ${err.status}\nMessage: ${err.message}`,
              );
            }
          }
        } else {
          this.toasterService.error('An error occurred');
        }
        return throwError(err);
      },
    );
  }
}
