import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserService} from './user.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private userService: UserService) {}

  exchangeCode(code: string) {
    this.http.post<any>('http://localhost:8081/api/auth/token', { code })
      .subscribe(res => {
        this.userService.storeToken(res.access_token);
        window.history.replaceState({}, '', '/');
      });
  }


}
