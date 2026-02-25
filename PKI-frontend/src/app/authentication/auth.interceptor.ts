import { inject } from '@angular/core';
import {HttpErrorResponse, HttpInterceptorFn, HttpRequest} from '@angular/common/http';
import { AuthService } from './auth.service';
import {catchError, from, switchMap, throwError} from 'rxjs';

export const authInterceptorFn: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);

  if (req.url.includes('/protocol/openid-connect/token')) {
    return next(req);
  }

  const token = auth.getAccessToken();

  if (token && auth.isTokenExpired()) {
    return from(auth.refreshAccessToken()).pipe(
      switchMap(newToken => {
        const authReq = req.clone({
          setHeaders: { Authorization: `Bearer ${newToken}` }
        });
        return next(authReq);
      }),
      catchError(err => {
        auth.login();
        return throwError(() => err);
      })
    );
  }
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
