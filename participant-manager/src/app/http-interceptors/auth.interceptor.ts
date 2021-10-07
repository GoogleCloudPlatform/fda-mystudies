import {Injectable} from '@angular/core';
import {NgxSpinnerService} from 'ngx-spinner';
import {
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import {filter, finalize, switchMap, take} from 'rxjs/operators';
import {BehaviorSubject, Observable, OperatorFunction, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ToastrService} from 'ngx-toastr';
import {getMessage} from '../shared/error.codes.enum';
import {AuthService} from '../service/auth.service';
import {ApiResponse} from '../entity/api.response.model';
import {environment} from '@environment';
import {CookieService} from 'ngx-cookie-service';
import {AccessToken} from '../entity/access-token';
import {Router} from '@angular/router';
import {
  GenericErrorCode,
  getGenericMessage,
} from '../shared/generic.error.codes.enum';
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;

  private readonly refreshTokenSubject = new BehaviorSubject<unknown>(null);
  appId = 'PARTICIPANT MANAGER';
  mobilePlatform = 'DESKTOP';
  source = 'PARTICIPANT MANAGER';

  constructor(
    private readonly spinner: NgxSpinnerService,
    private readonly toasterService: ToastrService,
    private readonly authService: AuthService,
    public cookieService: CookieService,
    private readonly router: Router,
  ) {}

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler,
  ): Observable<HttpEvent<unknown>> {
    void this.spinner.show();

    if (!this.authService.hasCredentials()) {
      return next.handle(this.setHeaders(req)).pipe(
        this.handleError(),
        finalize(() => {
          void this.spinner.hide();
        }),
      );
    }
    return next.handle(this.setHeaders(req)).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          return this.handle401Error(req, next);
        } else {
          return throwError(error);
        }
      }),
      this.handleError(),
      finalize(() => {
        void this.spinner.hide();
      }),
    );
  }
  private handle401Error(request: HttpRequest<unknown>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);
      return this.authService.refreshToken().pipe(
        switchMap((authServerResponse: AccessToken) => {
          this.isRefreshing = false;
          this.refreshTokenSubject.next(authServerResponse.access_token);
          sessionStorage.setItem(
            'accessToken',
            authServerResponse.access_token,
          );
          sessionStorage.setItem(
            'refreshToken',
            authServerResponse.refresh_token,
          );
          return next.handle(this.setHeaders(request));
        }),
        catchError((error: unknown) => {
          return throwError(error);
        }),
      );
    } else {
      return this.refreshTokenSubject.pipe(
        filter((token) => token !== null),
        take(1),
        switchMap(() => {
          return next.handle(this.setHeaders(request));
        }),
      );
    }
  }

  private setHeaders(req: HttpRequest<unknown>) {
    if (req.url.includes(`${environment.authServerUrl}`)) {
      let headers = req.headers
        .set('Accept', 'application/json')
        .set('correlationId', sessionStorage.getItem('correlationId') || '')
        .set('appId', this.authService.appId)
        .set('mobilePlatform', this.authService.mobilePlatform)
        .set('Access-Control-Allow-Origin', '*')
        .set(
          'Access-Control-Allow-Headers',
          'Origin, X-Requested-With, Content-Type, Accept',
        )
        .set(
          'Authorization',
          `Bearer ${sessionStorage.getItem('accessToken') || ''} `,
        )
        .set('appName', '')
        .set('contactEmail', '')
        .set('correlationId', sessionStorage.getItem('correlationId') || '')
        .set('appId', this.appId)
        .set('mobilePlatform', this.mobilePlatform)
        .set('source', this.source)
        .set('userId', sessionStorage.getItem('userId') || '')
        .set('appVersion', environment.appVersion || '');
      if (!req.headers.has('Content-Type')) {
        headers = headers.append(
          'Content-Type',
          'application/x-www-form-urlencoded',
        );
      }
      return req.clone({headers});
    } else {
      let headers = req.headers
        .set('userId', sessionStorage.getItem('userId') || '')
        .set('Access-Control-Allow-Origin', '*')
        .set(
          'Access-Control-Allow-Headers',
          'Origin, X-Requested-With, Content-Type, Accept',
        )
        .set(
          'Authorization',
          `Bearer ${sessionStorage.getItem('accessToken') || ''} `,
        )
        .set('correlationId', sessionStorage.getItem('correlationId') || '')
        .set('appId', this.appId)
        .set('mobilePlatform', this.mobilePlatform)
        .set('source', this.source)
        .set('appVersion', environment.appVersion || '');
      if (!req.headers.get('skipIfUpload')) {
        headers = headers.append('Content-Type', 'application/json');
      }
      return req.clone({headers});
    }
  }

  handleError<T>(): OperatorFunction<T, T> {
    return catchError(
      (err: unknown): Observable<T> => {
        if (err instanceof HttpErrorResponse) {
          if (err.url === `${environment.authServerUrl}/oauth2/token`) {
            sessionStorage.clear();
            void this.router.navigate(['/error/', 'EC_0080']);
          } else if (err.error instanceof ErrorEvent) {
            this.toasterService.error(err.error.message);
          } else {
            const customError = err.error as ApiResponse;
            if (getMessage(customError.error_code)) {
              if (
                customError.error_code !== 'EC_0070' &&
                customError.error_code !== 'EC_0071' &&
                customError.error_code !== 'EC_0072'
              ) {
                this.toasterService.error(getMessage(customError.error_code));
              }
            } else if (
              getGenericMessage(customError.error_code as GenericErrorCode)
            ) {
              void this.router.navigate(['/error/', customError.error_code]);
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
