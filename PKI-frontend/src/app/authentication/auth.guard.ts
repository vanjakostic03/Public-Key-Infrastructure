import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import {AuthService} from './auth.service';
import {UserService} from './user.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor( private router: Router, private userService: UserService, private authService: AuthService) { }

  canActivate(): boolean {
    if (!this.userService.hasToken()) {
      this.authService.login();
      return false;
    }
    return true;
  }

}
