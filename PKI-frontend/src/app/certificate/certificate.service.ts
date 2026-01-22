import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserService} from '../authentication/user.service';
import {Observable} from 'rxjs';
import {CertificateDTO} from '../authentication/dtos/CertificateDTO.model';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  private baseUrl = 'http://localhost:8081/api/certificates';

  constructor(private http: HttpClient, private userService: UserService) {}

  getAllCertificates(): Observable<CertificateDTO[]> {
    return this.http.get<CertificateDTO[]>(this.baseUrl, {
      headers: {
        Authorization: `Bearer ${this.userService.getToken()}`
      }
    });
  }
}
