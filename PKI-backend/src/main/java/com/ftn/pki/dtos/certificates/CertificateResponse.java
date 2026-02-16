package com.ftn.pki.dtos.certificates;

import com.ftn.pki.entities.certificates.Certificate;
import com.ftn.pki.entities.certificates.CertificateType;
import com.ftn.pki.entities.organizations.Organization;
import com.ftn.pki.entities.users.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class CertificateResponse {

    @Id
    private UUID id;
    private CertificateType type;
    private Date startDate;
    private Date endDate;
    private Map<String, String> extensions;
    private String commonName;
    private String surname;
    private String givenName;
    private String organization;
    private String organizationalUnit;
    private String country;
    private String email;
}
