package com.ftn.pki.services.certificates;
import com.ftn.pki.crypto.Issuer;
import com.ftn.pki.crypto.Subject;
import com.ftn.pki.dtos.certificates.CertificateRequest;
import com.ftn.pki.dtos.certificates.CertificateResponse;
import com.ftn.pki.dtos.certificates.DownloadRequest;
import com.ftn.pki.dtos.certificates.DownloadResponseDTO;
import com.ftn.pki.entities.certificates.Certificate;
import com.ftn.pki.entities.certificates.CertificateType;
import com.ftn.pki.entities.organizations.Organization;
import com.ftn.pki.exceptions.NotFoundException;
import com.ftn.pki.exceptions.crypto.DecryptionException;
import com.ftn.pki.exceptions.crypto.EncryptionException;
import com.ftn.pki.exceptions.crypto.SerialNumberConflictException;
import com.ftn.pki.exceptions.issuers.IssuerCannotIssueException;
import com.ftn.pki.exceptions.issuers.IssuerCertificateNotActiveException;
import com.ftn.pki.repositories.certificates.CertificateRepository;
import com.ftn.pki.repositories.organizations.OrganizationRepository;
import com.ftn.pki.util.Utils;
import jakarta.annotation.PostConstruct;
import org.aspectj.weaver.ast.Not;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class CertificateService {

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    OrganizationRepository organizationRepository;


    @Autowired
    private SecretKey masterKey;  // Inject bean iz SecurityConfig

    private Utils utils;

    @PostConstruct
    public void init() {
        this.utils = new Utils();
    }

    public Collection<CertificateResponse> findAll() {
        List<Certificate> certificates = certificateRepository.findAll();

        // Mapiraj Certificate entitete u CertificateResponse DTO-ove
        return certificates.stream()
                .map(cert -> {
                    try {
                        // Učitaj X509 sertifikat da izvučeš podatke
                        X509Certificate x509 = cert.getX509Certificate();
                        X500Name x500Name = Utils.getSubjectX500Name(x509);

                        return CertificateResponse.builder()
                                .id(cert.getId())
                                .type(cert.getType())
                                .startDate(cert.getStartDate())
                                .endDate(cert.getEndDate())
                                // Izvuci podatke iz X500Name
                                .commonName(Utils.getRDNValue(x500Name, BCStyle.CN))
                                .surname(Utils.getRDNValue(x500Name, BCStyle.SURNAME))
                                .givenName(Utils.getRDNValue(x500Name, BCStyle.GIVENNAME))
                                .organization(Utils.getRDNValue(x500Name, BCStyle.O))
                                .organizationalUnit(Utils.getRDNValue(x500Name, BCStyle.OU))
                                .country(Utils.getRDNValue(x500Name, BCStyle.C))
                                .email(Utils.getRDNValue(x500Name, BCStyle.E))
                                .extensions(null) // TODO: izvuci extensions iz sertifikata ako treba
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException("Error mapping certificate", e);
                    }
                })
                .collect(Collectors.toList());
    }

    public Certificate getById(UUID uuid) throws NotFoundException {
        return certificateRepository.getReferenceById(uuid);
    }

    @Transactional
    public CertificateResponse createCertificate(CertificateRequest dto) throws Exception{
        //todo
        // zastita od korisnika!!!!!

        if (dto.getStartDate() == null || dto.getEndDate() == null){
            throw new IllegalAccessException("Start and end dates must be provided");
        }
        if (dto.getStartDate().after( dto.getEndDate())){
            throw new IllegalAccessException("Start date is after end date");
        }
        if (dto.getCommonName() == null || dto.getOrganization() == null || dto.getCountry() == null || dto.getEmail() == null) {
            throw new IllegalAccessException("Common Name, Organization, Country, and Email are mandatory");
        }


        // Create rsa key pair for Subject and x500name
        // public key is for certificate so everyone can use it for encryption
        // private key is a secret only for subject
        KeyPair keyPair = Utils.generateKeys();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // distinguished name - has all data that identifies organization/user
        // entity identity
        X500Name x500Name =new X500NameBuilder()
                .addRDN(BCStyle.CN, dto.getCommonName())
                .addRDN(BCStyle.SURNAME, dto.getSurname())
                .addRDN(BCStyle.GIVENNAME, dto.getGivenName())
                .addRDN(BCStyle.O, dto.getOrganization())
                .addRDN(BCStyle.OU, dto.getOrganizationalUnit())
                .addRDN(BCStyle.C, dto.getCountry())
                .addRDN(BCStyle.E, dto.getEmail())
                .build();


        // create subject
        Subject subject = new Subject(publicKey, x500Name);

        // create issuer
        Issuer issuer;
        Certificate parent =null;       // reference to parent certificate (entity)
        //if certificate type is root CA then its self-signed
        if( dto.getRequestedType() == CertificateType.ROOT_CA && (dto.getParentId() == null || dto.getParentId().isEmpty())){
            issuer = new Issuer(privateKey, publicKey, x500Name);
        }else{
            // if not, find his parent, decrypt his private key so we can sign new certificate
             parent = certificateRepository.findById(UUID.fromString(dto.getParentId()))
                    .orElseThrow(() -> new NotFoundException("Certificate not found"));


            X509Certificate parentCertificate = parent.getX509Certificate();

            Date now = new Date();
            if(now.before(parentCertificate.getNotBefore()) || now.after(parentCertificate.getNotAfter())) {
                throw new IssuerCertificateNotActiveException("Parent certificate is not active");
            }

            if(parent.getType() == CertificateType.END_ENTITY) {
                throw new IssuerCannotIssueException("End Entity certificate cannot issue new certificates");
            }

            PrivateKey issuerPrivKeyDec;
            try {
                issuerPrivKeyDec = loadAndDecryptPrivateKey(parent);
            } catch(Exception e) {
                throw new DecryptionException("Cannot decrypt parent's private key");
            }
            issuer = new Issuer(issuerPrivKeyDec, parentCertificate.getPublicKey(), Utils.getSubjectX500Name(parentCertificate));
        }


        // create x509Cetificate
        X509Certificate x509Certificate = Utils.generateCertificate(
                subject,
                issuer,
                dto.getStartDate(),
                dto.getEndDate(),
                new BigInteger(64, new SecureRandom()).toString(),
                dto.getRequestedType(),
                dto.getParentId(),
                dto.getExtensions()
        );

        // find organization and DEK = data encryption Key - used for encrypting private keys for certificates
        // DEK - AES key
        // master key decrypts DEK

        Organization organization = organizationRepository.findByName(dto.getAssignToOrganizationName()).orElseThrow(() ->
                new NotFoundException("Organization with that name not found"));


        SecretKey organizationDEK = getOrganizationDEK(organization);

        // encryption of private key
        Utils.AESGcmEncrypted privateKeyEnc;
        try {
            privateKeyEnc = utils.encrypt(organizationDEK, Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        } catch(Exception e) {
            throw new EncryptionException("Cannot encrypt private key");
        }
        if(certificateRepository.existsBySerialNumber(x509Certificate.getSerialNumber().toString())) {
            throw new SerialNumberConflictException("Serial number conflict detected");
        }


        //map x509certificate on certificate entity
        Certificate certificate = Certificate.builder()
                .certificate(x509Certificate.getEncoded())
                .parent(parent)
                .organization(organization)
                .revoked(false)
                .type(dto.getRequestedType())
                .user(null)         //za sada null, posle adminkoji je ulogovan
                .serialNumber(x509Certificate.getSerialNumber().toString())
                .privateKeyEnc(Base64.getDecoder().decode(privateKeyEnc.getCiphertext()))
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .iv(privateKeyEnc.getIv())          // vector fo AES-GCM decryption
                .build();


        // save certificate entity
        certificateRepository.save(certificate);
        // map to certificate response
        return CertificateResponse.builder()
                .id(certificate.getId())
                .type(certificate.getType())
                .startDate(certificate.getStartDate())
                .endDate(certificate.getEndDate())
                .extensions(dto.getExtensions())
                .commonName(dto.getCommonName())
                .surname(dto.getSurname())
                .givenName(dto.getGivenName())
                .organization(dto.getOrganization())
                .organizationalUnit(dto.getOrganizationalUnit())
                .country(dto.getCountry())
                .email(dto.getEmail())
        .build();

    }

    @Transactional
    public DownloadResponseDTO download(DownloadRequest dto) throws Exception {

        // Find certificate
        Certificate certificateEntity = certificateRepository.findById(dto.getCertificateId())
                .orElseThrow(() -> new NotFoundException("Certificate not found"));


        // Generate X509 certificate and decrypt private key
        X509Certificate certificate = certificateEntity.getX509Certificate();

        PrivateKey privateKey = loadAndDecryptPrivateKey(certificateEntity);

        // Create PKCS12 keystore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null); // Initialize empty keystore

        // Add private key and certificate chain
        X509Certificate[] chain = new X509Certificate[]{certificate};
        keyStore.setKeyEntry(
                dto.getAlias(),
                privateKey,
                dto.getPassword().toCharArray(),
                chain
        );

        // Save to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        keyStore.store(baos, dto.getPassword().toCharArray());
        byte[] p12Bytes = baos.toByteArray();


        return DownloadResponseDTO.builder()
                .certificateBytes(p12Bytes)
                .fileName("certificate-" + certificateEntity.getSerialNumber() + ".p12")
                .build();
    }



    // ================== helpers

    private SecretKey getOrganizationDEK(Organization organization) throws Exception {
        String encryptedDEKBase64 = organization.getEncKey();
        Utils.AESGcmEncrypted encrypted = Utils.AESGcmEncrypted.builder()
                .ciphertext(encryptedDEKBase64)
                .iv(organization.getKeyIv())
                .build();
        String dekBase64 = utils.decrypt(masterKey, encrypted);
        return Utils.secretKeyFromBase64(dekBase64);
    }

    private PrivateKey loadAndDecryptPrivateKey(Certificate certEntity) throws Exception {
        byte[] encryptedPrivateKeyBytes = certEntity.getPrivateKeyEnc();
        String iv = certEntity.getIv();

        SecretKey organizationDEK = getOrganizationDEK(certEntity.getOrganization());
        Utils.AESGcmEncrypted encryptedPrivateKey = Utils.AESGcmEncrypted.builder()
                .ciphertext(Base64.getEncoder().encodeToString(encryptedPrivateKeyBytes))
                .iv(iv)
            .build();

        String decryptedPrivateKeyBase64 = utils.decrypt(organizationDEK, encryptedPrivateKey);
        return Utils.base64ToPrivateKey(decryptedPrivateKeyBase64);

    }




}
