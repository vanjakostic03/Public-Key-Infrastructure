import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';


@Injectable({ providedIn: 'root' })
export class AuthService {
  private clientId = 'pki-backend';
  private redirectUri = 'https://localhost:4200';
  private keycloakUrl = 'http://localhost:8080/realms/pki/protocol/openid-connect';

  constructor(private http: HttpClient) {}


  login(): void {
    if (sessionStorage.getItem('pkce_login_in_progress')) return;

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


  exchangeCode(code: string): Promise<void> {
    const codeVerifier = sessionStorage.getItem('pkce_verifier');

    if (!codeVerifier) {
      return Promise.reject('Code verifier missing');
    }

    const body = new URLSearchParams({
      grant_type: 'authorization_code',
      client_id: this.clientId,
      code: code,
      redirect_uri: this.redirectUri,
      code_verifier: codeVerifier
    });

    return this.http.post<any>(`${this.keycloakUrl}/token`, body.toString(), {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    }).toPromise()
      .then(res => {
        localStorage.setItem('accessToken', res.access_token);
        localStorage.setItem('refreshToken', res.refresh_token);
        sessionStorage.removeItem('pkce_login_in_progress');
        sessionStorage.removeItem('pkce_verifier');

        const url = new URL(window.location.href);
        url.searchParams.delete('code');
        url.searchParams.delete('state');
        window.history.replaceState({}, '', url.pathname);
      });
  }


  refreshAccessToken(): Promise<string | null> {
    const refreshToken = localStorage.getItem('refreshToken');

    if (!refreshToken) {
      return Promise.resolve(null);
    }

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
        this.logout();
        return null;
      });
  }

  isTokenExpired(): boolean {
    const token = this.getAccessToken();
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000;
      const now = Date.now();

      return now >= exp;
    } catch (e) {
      console.error('Error parsing token:', e);
      return true;
    }
  }

  getUserRoles(): string[] {
    const token = this.getAccessToken();
    if (!token) return [];

    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const payload = JSON.parse(window.atob(base64));

      const realmRoles = payload.realm_access?.roles || [];

      const clientRoles = payload.resource_access?.[this.clientId]?.roles || [];
      return [...realmRoles, ...clientRoles];
    } catch (e) {
      console.error('Error parsing token roles:', e);
      return [];
    }
  }

  hasRole(expectedRole: string): boolean {
    const roles = this.getUserRoles();
    return roles.includes(expectedRole);
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    window.location.href = `${this.keycloakUrl}/logout?redirect_uri=${encodeURIComponent(this.redirectUri)}`;
  }

  hasToken(): boolean {
    return !!this.getAccessToken();
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  private generateCodeVerifier(): string {
    const array = new Uint8Array(32);
    window.crypto.getRandomValues(array);
    return this.base64UrlEncode(array);
  }

  private base64UrlEncode(buffer: Uint8Array): string {
    const base64 = btoa(String.fromCharCode(...buffer));
    return base64
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=/g, '');
  }

  private async generateCodeChallenge(verifier: string): Promise<string> {
    const data = new TextEncoder().encode(verifier);
    const digest = await crypto.subtle.digest('SHA-256', data);
    return this.base64UrlEncode(new Uint8Array(digest));
  }
}
