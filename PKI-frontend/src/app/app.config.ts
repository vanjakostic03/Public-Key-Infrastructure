import { ApplicationConfig} from '@angular/core';
import {provideRouter} from '@angular/router';
import {routes} from './app-routing.module';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {authInterceptorFn} from './authentication/auth.interceptor';
import {AuthService} from './authentication/auth.service';
import {UserService} from './authentication/user.service';


export function initAuth(auth: AuthService) {
  return () => {};
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptorFn])),
    AuthService,
    UserService,
  ]
};

