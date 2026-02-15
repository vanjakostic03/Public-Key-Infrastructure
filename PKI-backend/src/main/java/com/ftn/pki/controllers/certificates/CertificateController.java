package com.ftn.pki.controllers.certificates;

import com.ftn.pki.dtos.certificates.CertificateRequest;
import com.ftn.pki.dtos.certificates.CertificateResponse;
import com.ftn.pki.dtos.certificates.DownloadRequest;
import com.ftn.pki.dtos.certificates.DownloadResponseDTO;
import com.ftn.pki.entities.certificates.Certificate;
import com.ftn.pki.services.certificates.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CertificateResponse>> getCertificates() {
        return ResponseEntity.ok(certificateService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateResponse> createCertificate(CertificateRequest dto){

        CertificateResponse response = null;
        try {
            response = certificateService.createCertificate(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download")
    public ResponseEntity<DownloadResponseDTO> downloadCertificate(@RequestBody DownloadRequest dto) {
        try {
            DownloadResponseDTO response = certificateService.download(dto);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + response.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
