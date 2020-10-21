import {Injectable} from '@angular/core';
import {NgxSpinnerService} from 'ngx-spinner';
import {
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import {finalize} from 'rxjs/operators';
import {BehaviorSubject, Observable, OperatorFunction, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ToastrService} from 'ngx-toastr';
import {getMessage} from '../shared/error.codes.enum';
import {AuthService} from '../service/auth.service';
import {ApiResponse} from '../entity/api.response.model';
import {environment} from 'src/environments/environment';
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
  private readonly refreshTokenSubject: BehaviorSubject<
    unknown
  > = new BehaviorSubject<unknown>(null);
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
      return next.handle(req).pipe(
        this.handleError(req, next),
        finalize(() => {
          void this.spinner.hide();
        }),
      );
    }

    return next.handle(this.setHeaders(req)).pipe(
      this.handleError(req, next),
      finalize(() => {
        void this.spinner.hide();
      }),
    );
  }
  private handle401Error(request: HttpRequest<unknown>, next: HttpHandler) {
    this.isRefreshing = true;
    this.refreshTokenSubject.next(null);
    return this.authService.refreshToken().subscribe(
      (authServerResponse: AccessToken) => {
        console.log('refresh token is successfull');
        this.refreshTokenSubject.next(authServerResponse);
        localStorage.setItem('accessToken', authServerResponse.access_token);
        localStorage.setItem(
          'refreshToken',
          authServerResponse.refresh_token,
        );
        return next.handle(this.setHeaders(request)).pipe(
          catchError((error: unknown) => {
            return throwError(error);
          }),
        );
      },
      (error: unknown) => {
        if (error instanceof HttpErrorResponse) {
          const customError = error.error as ApiResponse;
          if (getMessage(customError.error_code)) {
            this.toasterService.error('Session Expired');
          }
          localStorage.clear();
          void this.router.navigate(['/']);
        }
      },
    );
  }
  private setHeaders(req: HttpRequest<unknown>) {
    if (req.url.includes(`${environment.authServerUrl}`)) {
      let headers = req.headers
        .set('Accept', 'application/json')
        .set('correlationId', localStorage.getItem('correlationId') || '')
        .set('appId', this.authService.appId)
        .set('mobilePlatform', this.authService.mobilePlatform)
        .set('Access-Control-Allow-Origin', '*')
        .set(
          'Access-Control-Allow-Headers',
          'Origin, X-Requested-With, Content-Type, Accept',
        )
        .set(
          'Authorization',
          `Bearer ${localStorage.getItem('accessToken') || ''} `,
        );
      if (!req.headers.has('Content-Type')) {
        headers = headers.append(
          'Content-Type',
          'application/x-www-form-urlencoded',
        );
      }
      return req.clone({headers});
    } else {
      let headers = req.headers
        .set('userId', localStorage.getItem('userId') || '')
        .set('Access-Control-Allow-Origin', '*')
        .set(
          'Access-Control-Allow-Headers',
          'Origin, X-Requested-With, Content-Type, Accept',
        )
        .set(
          'Authorization',
          `Bearer ${localStorage.getItem('accessToken') || ''} `,
        );

      if (!req.headers.get('skipIfUpload')) {
        headers = headers.append('Content-Type', 'application/json');
      }
      return req.clone({headers});
    }
  }

  handleError<T>(
    request: HttpRequest<unknown>,
    next: HttpHandler,
  ): OperatorFunction<T, T> {
    return catchError(
      (err: unknown): Observable<T> => {
        console.log('in error handler :');
        console.log(err);
        if (err instanceof HttpErrorResponse) {
          if (err.status === 401) {
            this.handle401Error(request, next);
          } else if (err.error instanceof ErrorEvent) {
            this.toasterService.error(err.error.message);
          } else {
            const customError = err.error as ApiResponse;
            if (getMessage(customError.error_code)) {
              this.toasterService.error(getMessage(customError.error_code));
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
