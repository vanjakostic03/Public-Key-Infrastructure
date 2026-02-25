import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router} from '@angular/router';
import {AuthService} from './auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor( private router: Router, private authService: AuthService) { }

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const hasToken = this.authService.hasToken();
    const isExpired = this.authService.isTokenExpired();


    if (!hasToken || isExpired) {
      this.authService.login();
      return false;
    }
    const expectedRole = route.data['role'];

    if (expectedRole && !this.authService.hasRole(expectedRole)) {
      console.log("Access denied")
      this.router.navigate(['/']);
      return false;
    }


    return true;
  }

}
