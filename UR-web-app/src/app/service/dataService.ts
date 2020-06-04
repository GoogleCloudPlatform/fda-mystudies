import {Injectable} from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {HttpClient} from '@angular/common/http';
import {catchError, finalize} from 'rxjs/operators';
import {NgxSpinnerService} from 'ngx-spinner';
import {Router} from '@angular/router';
import {ConnectionService} from 'ng-connection-service';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Credentials': 'true',
    'Access-Control-Allow-Methods': 'GET, POST, OPTIONS, PUT, PATCH, DELETE',
  }),
};

@Injectable()
export class DataService {
  baseUrl = 'http://<IP>:<port>/urwebapp';
  headersFromService: {};
  serviceUrl: string;
  constructor(
    private connectionService: ConnectionService,
    private router: Router,
    private http: HttpClient,
    private spinner: NgxSpinnerService,
  ) {}

  httpPostRequest(
      bodydata: any,
      urlpath: string,
      headers: string,
  ): Observable<any> {
    this.spinner.show();
    this.serviceUrl = this.baseUrl + urlpath;
    this.prepareHeadrers(headers);
    return this.http.post<any>(this.serviceUrl, bodydata, httpOptions).pipe(
        catchError((err: HttpErrorResponse) => {
          if (err.status == 401) {
            window.localStorage.clear();
            this.router.navigate(['/login']);
          }
          return throwError(err);
        }),
        finalize(() => {
          this.spinner.hide();
        }),
    );
  }

  httpGetRequest(urlpath: string, headers: string): Observable<any> {
    this.spinner.show();
    this.serviceUrl = '';
    this.serviceUrl = this.baseUrl + urlpath;
    this.prepareHeadrers(headers);
    return this.http.get<any>(this.serviceUrl, httpOptions).pipe(
        catchError((err: HttpErrorResponse) => {
          if (err.status == 401) {
            window.localStorage.clear();
            this.router.navigate(['/login']);
          }
          return throwError(err);
        }),
        finalize(() => {
          this.spinner.hide();
        }),
    );
  }
  httpDeleteRequest(urlpath: string, headers: string): Observable<any> {
    this.spinner.show();
    this.serviceUrl = this.baseUrl + urlpath;
    this.prepareHeadrers(headers);
    return this.http.delete(this.serviceUrl, httpOptions).pipe(
        catchError((err: HttpErrorResponse) => {
          if (err.status == 401) {
            window.localStorage.clear();
            this.router.navigate(['/login']);
          }
          return throwError(err);
        }),
        finalize(() => {
          this.spinner.hide();
        }),
    );
  }
  httpPutRequest(
      bodydata: any,
      urlpath: string,
      headers: string,
  ): Observable<any> {
    this.spinner.show();
    this.serviceUrl = this.baseUrl + urlpath;
    this.prepareHeadrers(headers);

    return this.http.put<any>(this.serviceUrl, bodydata, httpOptions).pipe(
        catchError((err: HttpErrorResponse) => {
          if (err.status == 401) {
            window.localStorage.clear();
            this.router.navigate(['/login']);
          }
          return throwError(err);
        }),
        finalize(() => {
          this.spinner.hide();
        }),
    );
  }

  prepareHeadrers(headers) {
    httpOptions.headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Credentials': 'true',
      'Access-Control-Allow-Methods': 'GET, POST, OPTIONS, PUT, PATCH, DELETE',
    });
    if (headers != '' && headers != 'undefined' && headers != undefined) {
      this.headersFromService = JSON.parse(headers);
      if (Object.keys(this.headersFromService).length > 0) {
        for (const key in this.headersFromService) {
          if (key != undefined && key != '') {
            const value = this.headersFromService[key];
            httpOptions.headers = httpOptions.headers.append(key, value);
          }
        }
      }
    }
  }
}
