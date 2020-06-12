import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {LoginComponent} from
  './auth/login/login.component';
import {SetUpAccountComponent} from
  './auth/set-up-account/set-up-account.component';
import {ForgotPasswordComponent} from
  './auth/forgot-password/forgot-password.component';
import {HashLocationStrategy, LocationStrategy} from '@angular/common';
import {PageNotFoundComponent} from
  './page-not-found/page-not-found.component';
import {EntityService} from './service/entity.service';
import {httpInterceptorProviders} from './http-interceptors';
@NgModule({
  declarations: [LoginComponent, SetUpAccountComponent,
    ForgotPasswordComponent, AppComponent, PageNotFoundComponent],
  imports: [BrowserModule, AppRoutingModule],
  providers: [EntityService, httpInterceptorProviders
    {provide: LocationStrategy, useClass: HashLocationStrategy},
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
