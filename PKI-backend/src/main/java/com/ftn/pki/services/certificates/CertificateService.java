package com.ftn.pki.services.certificates;
import com.ftn.pki.crypto.Issuer;
import com.ftn.pki.crypto.Subject;
import com.ftn.pki.dtos.certificates.CertificateRequest;
import com.ftn.pki.dtos.certificates.CertificateResponse;
import com.ftn.pki.entities.certificates.Certificate;
import com.ftn.pki.repositories.certificates.CertificateRepository;
import com.ftn.pki.util.Utils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
public class CertificateService {

    @Autowired
    CertificateRepository certificateRepository;


    public Collection<CertificateResponse> findAll() {
        List<Certificate> certificateResponses = certificateRepository.findAll();
        return new ArrayList<>();
    }



    public CertificateResponse createCertificate(CertificateRequest dto) throws Exception{
        // kreiraj sertifikat
            // kreiraj parove kljuceva za subjecta
            // kreiraj subjecta
            // kreiraj issuera
            //generisi x509 sertifikat
            //ekriptuj priv kljuc
        //dodaj sve moguce provere

        KeyPair keyPair = Utils.generateKeys();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        X500Name x500Name =new X500NameBuilder()
                .addRDN(BCStyle.CN, dto.getCommonName())
                .addRDN(BCStyle.SURNAME, dto.getSurname())
                .addRDN(BCStyle.GIVENNAME, dto.getGivenName())
                .addRDN(BCStyle.O, dto.getOrganization())
                .addRDN(BCStyle.OU, dto.getOrganizationalUnit())
                .addRDN(BCStyle.C, dto.getCountry())
                .addRDN(BCStyle.E, dto.getEmail())
                .build();

        Subject subject = new Subject(publicKey, x500Name);

        Issuer issuer;
        Certificate parent =null;

        X509Certificate x509Certificate = Utils.generateCertificate(
            subject,
            issuer,
                dto.getStartDate(),
                dto.getEndDate(),
                new BigInteger(64, new SecureRandom()).toString(),
                dto.getRequestedType()
        );
        //sacuvaj sertiifkat
        //mapiraj i vrati response


    }
}
