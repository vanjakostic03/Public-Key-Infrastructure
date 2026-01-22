import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';


@Injectable({ providedIn: 'root' })
export class AuthService {
  private clientId = 'pki-backend';
  private redirectUri = 'http://localhost:4200';
  private keycloakUrl = 'http://localhost:8080/realms/pki/protocol/openid-connect';

  constructor(private http: HttpClient) {}


  login(): void {
    if (sessionStorage.getItem('pkce_login_in_progress')) return; // već redirectuje

    sessionStorage.setItem('pkce_login_in_progress', 'true');

    const codeVerifier = this.generateCodeVerifier();
    sessionStorage.setItem('pkce_verifier', codeVerifier);

    this.generateCodeChallenge(codeVerifier).then(codeChallenge => {
      const authUrl = `${this.keycloakUrl}/auth?` +
        `client_id=${this.clientId}` +
        `&response_type=code` +
        `&redirect_uri=${encodeURIComponent(this.redirectUri)}` +
        `&scope=openid` +
        `&code_challenge=${codeChallenge}` +
        `&code_challenge_method=S256`;

      window.location.href = authUrl;
    });
  }


  exchangeCode(code: string): void {
    const codeVerifier = sessionStorage.getItem('pkce_verifier')!;
    const body = new URLSearchParams({
      grant_type: 'authorization_code',
      client_id: this.clientId,
      code: code,
      redirect_uri: this.redirectUri,
      code_verifier: codeVerifier
    });

    this.http.post<any>(`${this.keycloakUrl}/token`, body.toString(), {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).subscribe(res => {
      localStorage.setItem('accessToken', res.access_token);
      localStorage.setItem('refreshToken', res.refresh_token);
      sessionStorage.removeItem('pkce_login_in_progress');
      sessionStorage.removeItem('pkce_verifier');
      window.history.replaceState({}, '', this.redirectUri);
    });
  }


  refreshAccessToken(): Promise<string | null> {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) return Promise.resolve(null);

    const body = new URLSearchParams({
      grant_type: 'refresh_token',
      client_id: this.clientId,
      refresh_token: refreshToken
    });

    return this.http.post<any>(`${this.keycloakUrl}/token`, body.toString(), {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).toPromise()
      .then(res => {
        localStorage.setItem('accessToken', res.access_token);
        if (res.refresh_token) {
          localStorage.setItem('refreshToken', res.refresh_token);
        }
        return res.access_token;
      })
      .catch(err => {
        console.error('Refresh token failed', err);
        this.logout();
        return null;
      });
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    window.location.href = `${this.keycloakUrl}/logout?redirect_uri=${encodeURIComponent(this.redirectUri)}`;
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  hasToken(): boolean {
    return !!this.getAccessToken();
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

