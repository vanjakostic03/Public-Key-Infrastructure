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
      try {
        await this.auth.exchangeCode(code);
        this.router.navigate(['/admin-home-dashboard']);
      } catch (error) {
        this.auth.login();
      }
    } else if (!this.auth.hasToken()) {
      this.auth.login();
    } else if (this.auth.isTokenExpired()) {
      const newToken = await this.auth.refreshAccessToken();
      if (newToken) {
        this.router.navigate(['/admin-home-dashboard']);
      } else {
        this.auth.login();
      }
    } else {
      this.router.navigate(['/admin-home-dashboard']);
    }
  }
  private redirectAfterLogin() {
    this.router.navigate(['/admin-home-dashboard']);
  }
}
