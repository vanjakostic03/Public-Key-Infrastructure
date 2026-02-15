package com.ftn.pki.dtos.certificates;

import com.ftn.pki.entities.certificates.Certificate;
import com.ftn.pki.entities.certificates.CertificateType;
import com.ftn.pki.entities.organizations.Organization;
import com.ftn.pki.entities.users.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class CertificateResponse {

    @Id
    private UUID id;
    private CertificateType type;
    private String serialNumber;
    private Date startDate;
    private Date endDate;
    private Organization organization;
    private User user;
    private Certificate issuer;
    private boolean revoked = false;
    private String revocationReason;
}
