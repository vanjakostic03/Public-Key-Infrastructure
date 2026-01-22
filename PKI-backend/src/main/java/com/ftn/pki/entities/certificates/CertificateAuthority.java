package com.ftn.pki.entities.certificates;

import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "certificate_authorities")
public class CertificateAuthority {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private CertificateType type; // ROOT or INTERMEDIATE

    @Column(nullable = false, unique = true)
    private String commonName;

    @Lob
    @Column(nullable = false)
    private String certificatePem;

    @Column(nullable = false)
    private String privateKeyAlias;


    private boolean revoked;
    private Date revokedAt;

    @ManyToOne
    private CertificateAuthority issuer;
}
