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
import {environment} from 'src/environments/environment';
import {CookieService} from 'ngx-cookie-service';
import {AccessToken} from '../entity/access-token';
import {Router} from '@angular/router';
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
    return this.authService
      .refreshToken()
      .pipe(
        switchMap((authServerResponse: AccessToken) => {
          console.log(authServerResponse);
          this.isRefreshing = false;
          this.refreshTokenSubject.next(authServerResponse);
          sessionStorage.setItem(
            'accessToken',
            authServerResponse.access_token,
          );
          sessionStorage.setItem(
            'refreshToken',
            authServerResponse.refresh_token,
          );
          return next.handle(this.setHeaders(request)).pipe(
            catchError((error) => {
              return throwError(error);
            }),
          );
        }),
      )
      .subscribe(
        (success) => {
          console.log('success');
          console.log(success);
        },
        (error) => {
          // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
          if (error.status === 401) {
            sessionStorage.clear();
            void this.router.navigate(['/']);
          }
        },
      );
    // }
    //  else {
    //   return this.refreshTokenSubject.pipe(
    //     filter((token) => token !== null),
    //     take(1),
    //     // switchMap((jwt) => {
    //     //   return next.handle(this.setHeaders(request));
    //     // }),
    //   );
    // }
  }
  private setHeaders(req: HttpRequest<unknown>) {
    if (req.url.includes(`${environment.authServerUrl}`)) {
      const headers = req.headers
        .set('Accept', 'application/json')
        .set('correlationId', sessionStorage.getItem('correlationId') || '')
        .set('appId', this.authService.appId)
        .set('mobilePlatform', this.authService.mobilePlatform)
        .set(
          'Authorization',
          `Bearer ${sessionStorage.getItem('accessToken') || ''} `,
        );
      if (!req.headers.has('Content-Type')) {
        headers.set('Content-Type', 'application/x-www-form-urlencoded');
      }
      return req.clone({headers});
    } else {
      const headers = req.headers
        .set('userId', sessionStorage.getItem('userId') || '')
        .set(
          'Authorization',
          `Bearer ${sessionStorage.getItem('accessToken') || ''} `,
        );
      if (!req.headers.has('Content-Type')) {
        req.headers.set('Content-Type', 'application/json');
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
        if (err instanceof HttpErrorResponse) {
          console.log(err.status);
          if (err.status === 0) {
            this.handle401Error(request, next);
          }
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
