
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class UserService {
  private tokenKey = 'authToken';

  storeToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  clearToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  hasToken(): boolean {
    return !!this.getToken();
  }
}
