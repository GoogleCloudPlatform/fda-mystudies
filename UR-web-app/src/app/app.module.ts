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
import {PageNotFoundComponentComponent} from
  './page-not-found-component/page-not-found-component.component';

@NgModule({
  declarations: [LoginComponent, SetUpAccountComponent,
    ForgotPasswordComponent, AppComponent, PageNotFoundComponentComponent],
  imports: [BrowserModule, AppRoutingModule],
  providers: [
    {provide: LocationStrategy, useClass: HashLocationStrategy},
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
