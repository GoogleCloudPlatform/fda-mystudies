import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {httpInterceptorProviders} from './http-interceptors';
import {EntityService} from './service/entity.service';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule],
  providers: [EntityService,  httpInterceptorProviders],
  bootstrap: [AppComponent],
})
export class AppModule {}
