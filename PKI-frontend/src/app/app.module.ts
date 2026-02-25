import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { AuthenticationModule } from './authentication/authentication.module';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptorFn } from './authentication/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    AuthenticationModule,
    // HttpClientModule
  ],
  providers: [
    provideHttpClient(
      withInterceptors([authInterceptorFn])
    )
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
