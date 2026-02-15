package com.ftn.pki.exceptions.issuers;

public class IssuerCertificateNotActiveException extends RuntimeException {
    public IssuerCertificateNotActiveException(String message) {
        super(message);
    }
}
