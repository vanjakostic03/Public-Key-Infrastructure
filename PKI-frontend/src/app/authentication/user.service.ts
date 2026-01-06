import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import {DecodedToken} from './models/DecodedToken.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  storeToken(token: string): void {
    localStorage.setItem('authToken', token);
  }

  getToken(): string | null {
    return localStorage.getItem('authToken');
  }



  getUserData(): any | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return {
        id: decoded.id,
        role: decoded.role,
        email: decoded['sub'],
      };
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  clearToken(): void {
    localStorage.removeItem('authToken');
  }
}
