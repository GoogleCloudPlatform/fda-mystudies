import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CookieService} from 'ngx-cookie-service';
import {User} from '../entity/user';
import {EntityService} from './entity.service';
import {Router} from '@angular/router';
import {AccessToken} from '../entity/AccessToken';
import {environment} from 'src/environments/environment';
import {ProfileService} from './profile.service';
import {UnsubscribeOnDestroyAdapter} from '../unsubscribe-on-destroy-adapter';

@Injectable({providedIn: 'root'})
export class AuthService extends UnsubscribeOnDestroyAdapter {
  constructor(
    private readonly http: HttpClient,
    public cookieService: CookieService,
    public entityService: EntityService<AccessToken>,
    public router: Router,
    private readonly profilService: ProfileService,
  ) {
    super();
  }
  checkCredentials(): boolean {
    return (
      this.cookieService.check('accessToken') &&
      this.cookieService.check('user')
    );
  }
  getLoggedInUserDetails(): User | null {
    if (this.cookieService.check('user')) {
      return JSON.parse(this.cookieService.get('user')) as User;
    }
    return null;
  }
  getLoggedInUserAccessToken(): string {
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
  getToken(code: string, userId: string): void {
    const payload = {
      grant_type: 'authorization_code',
      scope: 'openid',
      redirect_uri: environment.redirectUrl,
      code: code,
      userId: userId,
    };
    this.subs.add(
      this.entityService
        .authServerPost(JSON.stringify(payload), 'oauth2/token')
        .subscribe((response) => {
          this.cookieService.set('accessToken', response.accessToken);
          this.cookieService.set('refreshToken', response.refreshToken);
          this.getProfile();
        }),
    );
  }
  getProfile(): void {
    this.subs.add(
      this.profilService.getProfile().subscribe((user: User) => {
        this.cookieService.set('user', JSON.stringify(user));
        void this.router.navigate(['/coordinator/']);
      }),
    );
  }
}
