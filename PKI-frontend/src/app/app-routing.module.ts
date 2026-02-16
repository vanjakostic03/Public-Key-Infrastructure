import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AdminHomeDashboardComponent} from './admin-home-dashboard/admin-home-dashboard.component';
import {AuthGuard} from './authentication/auth.guard';


export const routes: Routes = [
  { path: 'admin-home-dashboard', component: AdminHomeDashboardComponent, canActivate: [AuthGuard] },

];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
