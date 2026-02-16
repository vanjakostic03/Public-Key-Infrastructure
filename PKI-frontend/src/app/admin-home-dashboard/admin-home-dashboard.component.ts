import { Component, OnInit } from '@angular/core';
import {
  CertificateRequest,
  CertificateResponse,
  CertificateService,
  DownloadRequest
} from '../certificate/certificate.service';
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
  showDownloadModal = false;

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

  selectedDownloadId: string | null = null;

  downloadForm = {
    alias: '',
    password: ''
  };

  constructor(private http: HttpClient, private certificateService: CertificateService) {
  }

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


  openDownloadModal(id: string) {
    this.selectedDownloadId = id;
    this.showDownloadModal = true;
  }

  closeDownloadModal() {
    this.showDownloadModal = false;
    this.selectedDownloadId = null;
    this.downloadForm.alias = '';
    this.downloadForm.password = '';
  }

  confirmDownload() {

    if (!this.selectedDownloadId) return;

    const dto: DownloadRequest = {
      certificateId: this.selectedDownloadId,
      alias: this.downloadForm.alias,
      password: this.downloadForm.password
    };

    this.certificateService.downloadCertificate(dto).subscribe({
      next: (blob: Blob) => {

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `certificate-${this.selectedDownloadId}.p12`;
        a.click();
        window.URL.revokeObjectURL(url);

        this.closeDownloadModal();
      },
      error: err => console.error(err)
    });
  }



}
