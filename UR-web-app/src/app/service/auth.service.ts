/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/naming-convention */
/* eslint-disable no-prototype-builtins */
import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {CookieService} from 'ngx-cookie-service';
import {User} from '../entity/user';
import {EntityService} from './entity.service';
import {Router, ActivatedRoute} from '@angular/router';
import {AccessToken} from '../entity/access-token';
import {environment} from 'src/environments/environment';
import {UserService} from './user.service';
import {v4 as uuidv4} from 'uuid';
import getPkce from 'oauth-pkce';
import {Observable} from 'rxjs';

@Injectable({providedIn: 'root'})
export class AuthService {
  constructor(
    private readonly http: HttpClient,
    public cookieService: CookieService,
    public entityService: EntityService<AccessToken>,
    public router: Router,
    public activatedRoute: ActivatedRoute,
    private readonly userService: UserService,
  ) {}

  storeDefaultsValues(): void {
    sessionStorage.setItem('tempRegId', '');
    if (!sessionStorage.hasOwnProperty('correlationId')) {
      sessionStorage.setItem('correlationId', uuidv4());
      getPkce(43, (error, {verifier, challenge}) => {
        if (!error) {
          sessionStorage.setItem('pkceVerifier', verifier);
          sessionStorage.setItem('pkceChallenge', challenge);
        }
      });
    }
  }

  grantAutoSignIn(): void {
    window.location.href = `${environment.loginUrl}?client_id=${
      environment.client_id
    }&scope=offline_access&response_type=code&appId=${
      environment.appId
    }&mobilePlatform=${environment.mobilePlatform}&tempRegId=${
      sessionStorage.getItem('tempRegId') || ''
    }&code_challenge_method=S256&code_challenge=${
      sessionStorage.getItem('pkceChallenge') || ''
    }&correlationId=${
      sessionStorage.getItem('correlationId') || ''
    }&redirect_uri=${environment.redirectUrl}&state=${uuidv4()}`;
  }

  hasCredentials(): boolean {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    return sessionStorage.hasOwnProperty('accessToken') !== null;
  }

  getUserAccessToken(): string {
    return sessionStorage.getItem('accessToken') || '';
  }
  getAuthUserId(): string {
    return sessionStorage.getItem('authUserId') || '';
  }

  grantAuthorization(code: string, userId: string): Observable<AccessToken> {
    const httpOptionsforauth = {
      headers: new HttpHeaders({
        'Content-Type': 'application/x-www-form-urlencoded',
        'Accept': 'application/json',
        'correlationId': `${sessionStorage.getItem('correlationId') || ''}`,
        'appId': `${environment.appId}`,
        'mobilePlatform': `${environment.mobilePlatform}`,
      }),
    };
    const payLoad = new HttpParams()
      .set(`grant_type`, 'authorization_code')
      .set('scope', 'openid offline offline_access')
      .set('code', code)
      .set('redirect_uri', `${environment.redirectUrl}`)
      .set('userId', userId)
      .set('code_verifier', `${sessionStorage.getItem('pkceVerifier') || ''}`);
    return this.http.post<AccessToken>(
      `${environment.authServerUrl}/oauth2/token`,
      payLoad.toString(),
      httpOptionsforauth,
    );
  }

  getUserDetails(): void {
    this.userService.getUserDetails().subscribe((user: User) => {
      this.cookieService.set('user', JSON.stringify(user));
      void this.router.navigate(['/coordinator/']);
    });
  }

  logOutUser(): void {
    sessionStorage.clear();
    this.cookieService.deleteAll();
  }
}
