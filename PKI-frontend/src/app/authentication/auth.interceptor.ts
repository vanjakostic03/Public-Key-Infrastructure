import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { AuthService } from './auth.service';
import { from, switchMap } from 'rxjs';

export const authInterceptorFn: HttpInterceptorFn = (req: HttpRequest<any>, next) => {
  const auth = inject(AuthService);

  return from(auth.refreshAccessToken()).pipe(
    switchMap(token => {
      const headers: { [name: string]: string } = token
        ? { Authorization: `Bearer ${token}` }
        : {};

      const authReq = req.clone({ setHeaders: headers });
      return next(authReq);
    })
  );
};
