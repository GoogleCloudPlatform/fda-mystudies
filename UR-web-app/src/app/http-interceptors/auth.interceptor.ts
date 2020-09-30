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
import {Observable, OperatorFunction, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {ToastrService} from 'ngx-toastr';
import {getMessage} from '../shared/error.codes.enum';
import {AuthService} from '../service/auth.service';
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
    void this.spinner.show();

    if (!this.authService.hasCredentials()) {
      return next.handle(req).pipe(
        this.handleError(),
        finalize(() => {
          void this.spinner.hide();
        }),
      );
    }
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
      const authReq = req.clone({headers});
      return next.handle(authReq).pipe(
        this.handleError(),
        finalize(() => {
          void this.spinner.hide();
        }),
      );
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
            console.log(customError.error_code);
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
