package com.ftn.pki.dtos.certificates;

import com.ftn.pki.crypto.Subject;
import com.ftn.pki.entities.certificates.CertificateType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Map;
@Data
@AllArgsConstructor
public class CertificateRequest{

    private String commonName;
    private String surname;
    private String givenName;

    private String organization;      // O
    private String organizationalUnit;// OU
    private String country;           // C
    private String email;             // E

    private Date startDate;
    private Date endDate;

    private CertificateType requestedType;

    private Map<String, String> extensions;

    private String assignToOrganizationName;
    private String parentId;        //who issued certificate - parent
}
