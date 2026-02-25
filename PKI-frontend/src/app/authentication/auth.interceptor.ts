import { inject } from '@angular/core';
import {HttpErrorResponse, HttpInterceptorFn, HttpRequest} from '@angular/common/http';
import { AuthService } from './auth.service';
import {catchError, from, switchMap, throwError} from 'rxjs';

export const authInterceptorFn: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.getAccessToken();


  console.log('🔵 Interceptor fired for:', req.url);
  console.log('🔵 Token present:', !!token);
  console.log('🔵 Token value:', token ? token.substring(0, 20) + '...' : 'null');

  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && token) {

        return from(auth.refreshAccessToken()).pipe(
          switchMap(newToken => {
            if (newToken) {
              const retryReq = req.clone({
                setHeaders: { Authorization: `Bearer ${newToken}` }
              });
              return next(retryReq);
            } else {
              auth.login();
              return throwError(() => error);
            }
          }),
          catchError(err => {
            auth.login();
            return throwError(() => error);
          })
        );
      }

      return throwError(() => error);
    })
  );
};
