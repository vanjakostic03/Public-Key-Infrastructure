import { Component } from '@angular/core';
import {ActivatedRoute, RouterOutlet} from '@angular/router';
import {AuthService} from './authentication/auth.service';
import {UserService} from './authentication/user.service';

@Component({
  selector: 'app-root',
  // standalone: true,
  // imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'PKI-frontend';

  constructor(private route: ActivatedRoute, private authService: AuthService, private userService: UserService) {}

  ngOnInit() {
    console.log("onitin")

    this.route.queryParams.subscribe(params => {
      console.log('queryParams:', params); // direktno loguje objekat u konzolu
      console.log('queryParams JSON:', JSON.stringify(params)); // pretvara u string za čitljiv prikaz

      if (params['code']) {
        console.log("if");
        this.authService.exchangeCode(params['code']);
      } else if (!this.userService.getToken()) {
        console.log("elseif");
        this.authService.login();
      }
    });
  }

}
