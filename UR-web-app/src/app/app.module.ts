import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {EntityService} from './service/entity.service';
import {httpInterceptorProviders} from './http-interceptors';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule],
  providers: [EntityService, httpInterceptorProviders],
  bootstrap: [AppComponent],
})
export class AppModule {}
