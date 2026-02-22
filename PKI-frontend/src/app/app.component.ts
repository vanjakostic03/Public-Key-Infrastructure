import {Component, OnInit} from '@angular/core';
import {AuthService} from './authentication/auth.service';
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  async ngOnInit() {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');

    if (code) {
      console.log('Code detected, exchanging...');
      try {
        await this.auth.exchangeCode(code);
        console.log('Token stored successfully');
        this.router.navigate(['/admin-home-dashboard']);
      } catch (error) {
        console.error('Token exchange failed:', error);
        this.auth.login();
      }
    } else if (!this.auth.hasToken()) {
      console.log('No token, logging in...');
      this.auth.login();
    } else if (this.auth.isTokenExpired()) {
      console.log('Token expired, refreshing...');
      const newToken = await this.auth.refreshAccessToken();
      if (newToken) {
        console.log('Token refreshed, redirecting...');
        this.router.navigate(['/admin-home-dashboard']);
      } else {
        console.log('Refresh failed, logging in...');
        this.auth.login();
      }
    } else {
      console.log('Token exists and valid, redirecting...');
      this.router.navigate(['/admin-home-dashboard']);
    }
  }
  private redirectAfterLogin() {
    console.log('Redirecting to admin dashboard');
    this.router.navigate(['/admin-home-dashboard']);
  }
}
