import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Location} from '@angular/common';

@Injectable()
export class EntityService {
  baseUrl = 'http://<IP>:<port>/urwebapp';
  serviceUrl: string = '';
  constructor(private http: HttpClient) {}

  post(bodydata: any, urlpath: string): Observable<any> {
    this.serviceUrl = Location.joinWithSlash(this.baseUrl, urlpath);
    return this.http.post<any>(this.serviceUrl, bodydata).pipe();
  }
  get(urlpath: string): Observable<any> {
    this.serviceUrl = Location.joinWithSlash(this.baseUrl, urlpath);
    return this.http.get<any>(this.serviceUrl).pipe();
  }
  delete(urlpath: string): Observable<any> {
    this.serviceUrl = Location.joinWithSlash(this.baseUrl, urlpath);
    return this.http.delete(this.serviceUrl).pipe();
  }
  put(bodydata: any, urlpath: string): Observable<any> {
    this.serviceUrl = Location.joinWithSlash(this.baseUrl, urlpath);
    return this.http.put<any>(this.serviceUrl, bodydata).pipe();
  }
}
