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
  pkceLength = 43;
  appId = 'PARTICIPANT MANAGER';
  mobilePlatform = 'DESKTOP';
  source = 'PARTICIPANT MANAGER';

  constructor(
    private readonly http: HttpClient,
    public cookieService: CookieService,
    public entityService: EntityService<AccessToken>,
    public router: Router,
    public activatedRoute: ActivatedRoute,
    private readonly userService: UserService,
  ) {}

  initlocalStorage(): void {
    localStorage.setItem('correlationId', uuidv4());
    getPkce(this.pkceLength, (error, {verifier, challenge}) => {
      if (!error) {
        localStorage.setItem('pkceVerifier', verifier);
        localStorage.setItem('pkceChallenge', challenge);
      }
    });
  }

  beginLoginConsentFlow(): void {
    const params = new HttpParams()
      .set('client_id', environment.clientId)
      .set('scope', 'offline_access')
      .set('appId', this.appId)
      .set('response_type', 'code')
      .set('mobilePlatform', this.mobilePlatform)
      .set('code_challenge_method', 'S256')
      .set('code_challenge', localStorage.getItem('pkceChallenge') || '')
      .set('correlationId', localStorage.getItem('correlationId') || '')
      .set('tempRegId', localStorage.getItem('tempRegId') || '')
      .set('redirect_uri', environment.redirectUrl)
      .set('state', uuidv4())
      .set('source', this.source)
      .toString();
    window.location.href = `${environment.loginUrl}?${params}`;
  }

  hasCredentials(): boolean {
    return 'accessToken' in localStorage;
  }

  getUserAccessToken(): string {
    return localStorage.getItem('accessToken') || '';
  }
  getAuthUserId(): string {
    return localStorage.getItem('authUserId') || '';
  }

  getUserId(): string {
    return localStorage.getItem('userId') || '';
  }

  getToken(code: string, userId: string): Observable<AccessToken> {
    const options = {
      headers: new HttpHeaders({
        /* eslint-disable @typescript-eslint/naming-convention */
        'Content-Type': 'application/x-www-form-urlencoded',
        'Accept': 'application/json',
        'correlationId': localStorage.getItem('correlationId') || '',
        'appId': this.appId,
        'mobilePlatform': this.mobilePlatform,
      }),
    };
    const params = new HttpParams()
      .set(`grant_type`, 'authorization_code')
      .set('scope', 'openid offline offline_access')
      .set('code', code)
      .set('redirect_uri', environment.redirectUrl)
      .set('userId', userId)
      .set('code_verifier', localStorage.getItem('pkceVerifier') || '');
    return this.http.post<AccessToken>(
      `${environment.authServerUrl}/oauth2/token`,
      params.toString(),
      options,
    );
  }

  refreshToken(): Observable<AccessToken> {
    const options = {
      headers: new HttpHeaders({
        /* eslint-disable @typescript-eslint/naming-convention */
        'Content-Type': 'application/x-www-form-urlencoded',
        'Accept': 'application/json',
        'correlationId': localStorage.getItem('correlationId') || '',
        'appId': this.appId,
        'mobilePlatform': this.mobilePlatform,
      }),
    };

    const params = new HttpParams()
      .set(`grant_type`, 'refresh_token')
      .set('redirect_uri', environment.redirectUrl)
      .set('client_id', environment.clientId)
      .set(`refresh_token`, this.getRefreshToken())
      .set('userId', this.getAuthUserId());

    return this.http.post<AccessToken>(
      `${environment.authServerUrl}/oauth2/token`,
      params.toString(),
      options,
    );
  }

  private getRefreshToken() {
    return localStorage.getItem('refreshToken') || '';
  }

  getUserDetails(): void {
    this.userService.getUserDetails().subscribe((user: User) => {
      this.cookieService.set('user', JSON.stringify(user));
      void this.router.navigate(['/coordinator/studies/sites']);
    });
  }

  logOutUser(): void {
    localStorage.clear();
    this.cookieService.deleteAll();
  }
}
