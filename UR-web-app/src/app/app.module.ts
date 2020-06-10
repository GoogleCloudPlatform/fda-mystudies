import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {EntityService} from './service/entity.service';
import {AuthService} from '../app/service/auth.service';
import {httpInterceptorProviders} from './http-interceptors/index';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule],
  providers: [EntityService, AuthService, httpInterceptorProviders],
  bootstrap: [AppComponent],
})
export class AppModule {}
