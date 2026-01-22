package com.ftn.pki.entities.certificates;

import com.ftn.pki.entities.organizations.Organization;
import com.ftn.pki.entities.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
//@Table("certificates")
public class Certificate {

    @Id
    private UUID id;

    @ManyToOne
    private CertificateAuthority issuerCa;

    private String subjectDn;

    @Lob
    private String certificatePem;

    private String serialNumber;

    private Date validFrom;
    private Date validTo;

    @Enumerated(EnumType.STRING)
    private CertificateType type; // ROOT, INTERMEDIATE, EE

    private boolean revoked;
    private Date revokedAt;
    private String revocationReason;



}
