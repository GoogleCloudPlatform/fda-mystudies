// import {Injectable} from '@angular/core';
// import {HttpHeaders, HttpRequest} from '@angular/common/http';
// import {environment} from '@environment';

// @Injectable({
//   providedIn: 'root',
// })
// export class CommonUtilService {
//   constructor() {}

//   addAuditEventHeaders(headers: HttpHeaders): void {
//     headers
//       .set('correlationId', sessionStorage.getItem('correlationId') || '')
//       .set('appId', `${environment.appId}`)
//       .set('source', `${environment.source}`)
//       .set('mobilePlatform', `${environment.mobilePlatform}`);
//   }

//   getCommonHeaders(req: HttpRequest<unknown>): HttpHeaders {
//     const headers = this.getDefaultHeaders(req);
//     this.addAuditEventHeaders(headers);
//     this.addOptionalHeaders(headers);
//     return headers;
//   }

//   addOptionalHeaders(headers: HttpHeaders): void {
//     if (sessionStorage.hasOwnProperty('userId')) {
//       headers.set('userId', sessionStorage.getItem('userId') || '');
//     }
//     if (sessionStorage.hasOwnProperty('authUserId')) {
//       headers.set('authUserId', sessionStorage.getItem('authUserId') || '');
//     }
//     if (sessionStorage.hasOwnProperty('accessToken')) {
//       headers.set(
//         'Authorization',
//         'Bearer ' + sessionStorage.getItem('accessToken') || '',
//       );
//     }
//   }

//   getDefaultHeaders(req: HttpRequest<unknown>): HttpHeaders {
//     let headers = req.headers
//       .set('Content-Type', 'application/json')
//       .set('Accept', 'application/json');
//     this.addOptionalHeaders(headers);
//     return headers;
//   }
// }
