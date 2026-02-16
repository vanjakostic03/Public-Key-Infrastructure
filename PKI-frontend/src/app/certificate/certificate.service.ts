import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserService} from '../authentication/user.service';
import {Observable} from 'rxjs';
import {AuthService} from '../authentication/auth.service';

export type CertificateType = 'ROOT_CA' | 'INTERMEDIATE' | 'END_ENTITY';

export interface CertificateResponse {
  id: string;
  type: CertificateType;
  startDate: Date;
  endDate: Date;
  extensions: { [key: string]: string };
  commonName: string;
  surname: string;
  givenName: string;
  organization: string;
  organizationalUnit: string;
  country: string;
  email: string;
}


export interface CertificateRequest {
  commonName: string;
  surname: string;
  givenName: string;

  organization: string;
  organizationalUnit: string;
  country: string;
  email: string;

  startDate: Date;
  endDate: Date;

  requestedType: CertificateType;

  extensions: { [key: string]: string };

  assignToOrganizationName: string;
  parentId: string | null;
}



@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  private baseUrl = 'http://localhost:8081/api/certificates';

  constructor(private http: HttpClient, private authService: AuthService) {}

  getAllCertificates(): Observable<CertificateResponse[]> {
    return this.http.get<CertificateResponse[]>(this.baseUrl, {
      headers: {
        Authorization: `Bearer ${this.authService.getAccessToken()}`
      }
    });
  }

  createCertificate(request: CertificateRequest) {
    return this.http.post<CertificateResponse>(
      this.baseUrl,
      request,
      {
        headers: {
          Authorization: `Bearer ${this.authService.getAccessToken()}`
        }
      }
    );
  }

}
