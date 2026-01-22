import {Component, OnInit} from '@angular/core';
import {AuthService} from './authentication/auth.service';
import {UserService} from './authentication/user.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  constructor(private auth: AuthService, private user: UserService) {}

  ngOnInit() {
    const url = new URL(window.location.href);
    const code = url.searchParams.get('code');
    if (code) {
      this.auth.exchangeCode(code);
    } else if (!this.auth.hasToken()) {
      this.auth.login();
    }
  }
}
