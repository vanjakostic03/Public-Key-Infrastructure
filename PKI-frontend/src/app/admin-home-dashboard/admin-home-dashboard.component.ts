import { Component, OnInit } from '@angular/core';
import {CertificateRequest, CertificateResponse, CertificateService} from '../certificate/certificate.service';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-home-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-home-dashboard.component.html',
  styleUrls: ['./admin-home-dashboard.component.css']
})
export class AdminHomeDashboardComponent implements OnInit {

  certificates: CertificateResponse[] = [];
  selectedCertificate: CertificateResponse | null = null;

  showCreateModal = false;

  createForm: any = {
    requestedType: '',
    parentId: null,
    commonName: '',
    surname: '',
    givenName: '',
    organization: '',
    organizationalUnit: '',
    country: '',
    email: '',
    startDate: '',
    endDate: '',
    extensions: {}
  };


  constructor(private http: HttpClient, private certificateService: CertificateService) {}

  ngOnInit(): void {
    this.loadCertificates();
  }

  loadCertificates() {
    this.certificateService.getAllCertificates().subscribe({
      next: (certificates: CertificateResponse[]) => {
        this.certificates = certificates;
      },
      error: err => {
        console.log(err);
      }
    });
  }

  downloadCertificate(id: string) {
    this.http.get(`/api/admin/certificates/${id}/download`, {
      responseType: 'blob'
    }).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'certificate.p12';
      a.click();
    });
  }


  createCertificate(){
    const dto: CertificateRequest = {
      commonName: this.createForm.commonName,
      surname: this.createForm.surname,
      givenName: this.createForm.givenName,
      organization: this.createForm.organization,
      organizationalUnit: this.createForm.organizationalUnit,
      country: this.createForm.country,
      email: this.createForm.email,
      startDate: new Date(this.createForm.startDate),
      endDate: new Date(this.createForm.endDate),
      requestedType: this.createForm.requestedType as 'ROOT_CA' | 'INTERMEDIATE' | 'END_ENTITY',
      parentId: this.createForm.requestedType === 'ROOT_CA' ? null : this.createForm.parentId,
      assignToOrganizationName: this.createForm.organization, // mora biti obavezno
      extensions: {
        basicConstraints: (!!this.createForm.basicConstraints).toString(),
        keyCertSign: (!!this.createForm.keyCertSign).toString(),
        digitalSignature: (!!this.createForm.digitalSignature).toString()
      }
    };

    this.certificateService.createCertificate(dto).subscribe({
      next: () => {
        this.closeCreateModal();
        this.loadCertificates();
      },
      error: err => console.log(err)
    });
  }


  showDetails(cert: CertificateResponse) {
    this.selectedCertificate = cert;
  }

  closeDetails() {
    this.selectedCertificate = null;
  }

  openCreateModal() {
    this.showCreateModal = true;
  }

  closeCreateModal() {
    this.showCreateModal = false;
    this.showCreateModal = false;
  }

  get issuerCertificates() {
    return this.certificates.filter(c => c.type !== 'END_ENTITY');
  }



}
