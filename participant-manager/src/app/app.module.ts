import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {LoginComponent} from './auth/login/login.component';
import {SetUpAccountComponent} from './auth/set-up-account/set-up-account.component';
import {ForgotPasswordComponent} from './auth/forgot-password/forgot-password.component';
import {
  HashLocationStrategy,
  LocationStrategy,
  APP_BASE_HREF,
} from '@angular/common';
import {PageNotFoundComponent} from './page-not-found/page-not-found.component';
import {EntityService} from './service/entity.service';
import {httpInterceptorProviders} from './http-interceptors';
import {ToastrModule} from 'ngx-toastr';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgxSpinnerModule} from 'ngx-spinner';
import {HttpClientModule, HttpClientXsrfModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ModalModule, BsModalRef} from 'ngx-bootstrap/modal';
import {LoginCallbackComponent} from './auth/login-callback/login-callback.component';
import {CookieService} from 'ngx-cookie-service';
import {ErrorComponent} from './error/error.component';
import {TermsComponent} from './terms/terms.component';
import {AboutComponent} from './about/about.component';
import {environment} from '@environment';

@NgModule({
  declarations: [
    LoginComponent,
    SetUpAccountComponent,
    ForgotPasswordComponent,
    AppComponent,
    PageNotFoundComponent,
    LoginCallbackComponent,
    ErrorComponent,
    TermsComponent,
    AboutComponent,
  ],
  imports: [
    ModalModule.forRoot(),
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    NgxSpinnerModule,
    HttpClientModule,
    HttpClientXsrfModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot({
      positionClass: 'toast-top-center',
      preventDuplicates: true,
      enableHtml: true,
      maxOpened: 1,
    }),
  ],
  providers: [
    BsModalRef,
    CookieService,
    EntityService,
    BsModalRef,
    httpInterceptorProviders,
    {provide: LocationStrategy, useClass: HashLocationStrategy},
    {provide: APP_BASE_HREF, useValue: environment.baseHref},
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
