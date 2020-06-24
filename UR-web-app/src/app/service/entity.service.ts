import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Location} from '@angular/common';

@Injectable()
export class EntityService<T> {
  baseUrl = 'http://<IP>:<port>/urwebappws';

  constructor(private readonly http: HttpClient) {}

  post(bodydata: string, urlpath: string): Observable<T> {
    const serviceUrl = Location.joinWithSlash(this.baseUrl, urlpath);
    return this.http.post<T>(serviceUrl, bodydata);
  }
  get(urlpath: string): Observable<T> {
    const serviceUrl = Location.joinWithSlash(this.baseUrl, urlpath);
    return this.http.get<T>(serviceUrl);
  }
  delete(urlpath: string): Observable<T> {
    const serviceUrl = Location.joinWithSlash(this.baseUrl, urlpath);
    return this.http.delete<T>(serviceUrl);
  }
  put(bodydata: string, urlpath: string): Observable<T> {
    const serviceUrl = Location.joinWithSlash(this.baseUrl, urlpath);
    return this.http.put<T>(serviceUrl, bodydata);
  }
}
