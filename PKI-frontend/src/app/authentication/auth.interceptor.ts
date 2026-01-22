import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';
import { UserService } from './user.service';

export const authInterceptorFn: HttpInterceptorFn = (req, next) => {
  const user = inject(UserService);
  const token = user.getToken();

  if (token) {
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(authReq);
  }

  return next(req);
};
