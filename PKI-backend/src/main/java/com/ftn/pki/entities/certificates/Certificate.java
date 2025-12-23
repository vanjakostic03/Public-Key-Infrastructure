package com.ftn.pki.entities.certificates;

import com.ftn.pki.entities.organizations.Organization;
import com.ftn.pki.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Certificate {
    private Organization issuer;
    private User subject;
    private Date startDate;
    private Date endDate;
    private CertificateType certificateType;
    private Boolean isRevoked;
    private Boolean basicConstraints;
    private Certificate parent;



}
