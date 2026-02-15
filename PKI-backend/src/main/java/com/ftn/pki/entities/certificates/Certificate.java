package com.ftn.pki.entities.certificates;

import com.ftn.pki.crypto.Issuer;
import com.ftn.pki.crypto.Subject;
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
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CertificateType type;

    @Column(nullable = false)
    private Date startDate;
    @Column(nullable = false)
    private Date endDate;

    //certificate can be issued to user or organization
    // which organization
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    //which user
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String serialNumber;

    //who issued certificate
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private Certificate parent;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(nullable = true)
    private String revocationReason;

    @Lob    //large data annotation
    private byte[] certificate; // X509Certificate

    @Lob
    private byte[] privateKeyEnc;


}
