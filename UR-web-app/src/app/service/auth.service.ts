import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CookieService} from 'ngx-cookie-service';
import {User} from '../entity/user';
import {EntityService} from './entity.service';
import {Router} from '@angular/router';
import {AccessToken} from '../entity/access-token';
import {environment} from 'src/environments/environment';
import {UserService} from './user.service';

@Injectable({providedIn: 'root'})
export class AuthService {
  constructor(
    private readonly http: HttpClient,
    public cookieService: CookieService,
    public entityService: EntityService<AccessToken>,
    public router: Router,
    private readonly userService: UserService,
  ) {}
  hasCredentials(): boolean {
    return (
      this.cookieService.check('accessToken') &&
      this.cookieService.check('user') &&
      JSON.parse(this.cookieService.get('user')) !== null
    );
  }
  getUser(): User {
    return JSON.parse(this.cookieService.get('user')) as User;
  }
  getUserAccessToken(): string {
    return this.cookieService.get('accessToken');
  }

  redirectToLoginPage(): void {
    void this.router.navigate(['/callback'], {
      queryParams: {
        code: 'l4hQQM-guBDwbpo76dHJs-8.ufR0YLll0menniGz5A-YN95DfCFOKg',
        userId:
          'b45bc4f67fd77ebb6db9a94f8abd9a0470b07f4098f7934234f12a5bee975231',
      },
    });
  }
  grantAuthorization(code: string, userId: string): void {
    const payload = {
      // eslint-disable-next-line @typescript-eslint/naming-convention
      grant_type: 'authorization_code',
      scope: 'openid',
      // eslint-disable-next-line @typescript-eslint/naming-convention
      redirect_uri: environment.redirectUrl,
      code: code,
      userId: userId,
    };
    this.http
      .post<AccessToken>(`${environment.authServerUrl}/oauth2/token`, payload)
      .subscribe((response) => {
        this.cookieService.set('accessToken', response.accessToken);
        this.cookieService.set('refreshToken', response.refreshToken);
        this.getUserDetails();
      });
  }
  getUserDetails(): void {
    this.userService.getUserDetails().subscribe((user: User) => {
      this.cookieService.set('user', JSON.stringify(user));
      void this.router.navigate(['/coordinator/']);
    });
  }
}
