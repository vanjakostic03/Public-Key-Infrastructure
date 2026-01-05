package com.ftn.pki.controllers.certificates;

import com.ftn.pki.dtos.certificates.CertificateDTO;
import com.ftn.pki.entities.certificates.Certificate;
import com.ftn.pki.services.certificates.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {
//    @Autowired
//    private CertificateService certificateService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<CertificateDTO>> getCertificates() {
//        List<SimpleUserDTO> users = certificateService.findAll();
        List<CertificateDTO> certificates = new ArrayList<>();
        if(certificates == null){
            return new ResponseEntity<Collection<CertificateDTO>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(certificates, HttpStatus.OK);
    }
}
