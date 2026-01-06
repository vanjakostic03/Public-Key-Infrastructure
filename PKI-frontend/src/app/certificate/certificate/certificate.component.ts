import { Component } from '@angular/core';
import {CertificateDTO} from '../../authentication/dtos/CertificateDTO.model';
import {CertificateService} from '../certificate.service';

@Component({
  selector: 'app-certificate',
  templateUrl: './certificate.component.html',
  styleUrl: './certificate.component.css'
})
export class CertificateComponent {

  certificates: CertificateDTO[] = [];

  constructor(private certificateService: CertificateService) {}

  ngOnInit() {
    this.certificateService.getAllCertificates().subscribe({
      next: (data) => this.certificates = data,
      error: (err) => console.error('Error loading certificates', err)
    });
  }
}
