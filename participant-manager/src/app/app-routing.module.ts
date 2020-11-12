import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {LoginComponent} from './auth/login/login.component';
import {SetUpAccountComponent} from './auth/set-up-account/set-up-account.component';
import {ForgotPasswordComponent} from './auth/forgot-password/forgot-password.component';
import {PageNotFoundComponent} from './page-not-found/page-not-found.component';
import {SiteCoordinatorModule} from './site-coordinator/site-coordinator.module';
import {LoginCallbackComponent} from './auth/login-callback/login-callback.component';
import {AuthGuard} from './service/auth.guard';
import {ErrorComponent} from './error/error.component';
import {TermsComponent} from './terms/terms.component';
import {AboutComponent} from './about/about.component';

const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'forgotPassword', component: ForgotPasswordComponent},
  {path: 'set-up-account/:securityCode', component: SetUpAccountComponent},
  {
    path: 'callback',
    component: LoginCallbackComponent,
  },
  {
    path: 'coordinator',
    loadChildren: async (): Promise<SiteCoordinatorModule> =>
      import('./site-coordinator/site-coordinator.module').then(
        (m) => m.SiteCoordinatorModule,
      ),
    canActivate: [AuthGuard],
  },
  {path: 'error/:errorCode', component: ErrorComponent},
  {path: 'terms', component: TermsComponent},
  {path: 'about', component: AboutComponent},
  {path: '', redirectTo: '/login', pathMatch: 'full'},
  // {path: '**', component: PageNotFoundComponent},
  {path: 'pagenotfound', component: PageNotFoundComponent},
  {path: '**', redirectTo: 'pagenotfound'},
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
