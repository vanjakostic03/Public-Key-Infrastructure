import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserService} from './user.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private userService: UserService) {}

  private clientId = 'pki-frontend';
  private redirectUri = 'http://localhost:4200';

  login() {
    const verifier = this.generateCodeVerifier();
    sessionStorage.setItem('pkce_verifier', verifier);

    this.generateCodeChallenge(verifier).then(challenge => {
      const authUrl = `http://localhost:8080/realms/pki/protocol/openid-connect/auth?` +
        `client_id=${this.clientId}` +
        `&response_type=code` +
        `&redirect_uri=${encodeURIComponent(this.redirectUri)}` +
        `&scope=openid` +
        `&code_challenge=${challenge}` +
        `&code_challenge_method=S256`;

      console.log('Redirecting to Keycloak:', authUrl); // ovde vidiš tačan URL
      window.location.href = authUrl;
    });
  }


  exchangeCode(code: string) {
    const body = new URLSearchParams({
      grant_type: 'authorization_code',
      client_id: 'pki-frontend',
      code: code,
      redirect_uri: 'http://localhost:4200',
      code_verifier: sessionStorage.getItem('pkce_verifier')!
    });

    this.http.post<any>(
      'http://localhost:8080/realms/pki/protocol/openid-connect/token',
      body.toString(),
      {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      }
    ).subscribe(res => {
      this.userService.storeToken(res.access_token);
      window.history.replaceState({}, '', '/');
    });
  }

  private generateCodeVerifier(): string {
      const array = new Uint8Array(32);
      window.crypto.getRandomValues(array);
      return Array.from(array, b => b.toString(16).padStart(2, '0')).join('');
    }

  private async generateCodeChallenge(verifier: string): Promise<string> {
      const data = new TextEncoder().encode(verifier);
      const digest = await crypto.subtle.digest('SHA-256', data);

      return btoa(String.fromCharCode(...new Uint8Array(digest)))
        .replace(/\+/g, '-')
        .replace(/\//g, '_')
        .replace(/=+$/, '');
    }



}
