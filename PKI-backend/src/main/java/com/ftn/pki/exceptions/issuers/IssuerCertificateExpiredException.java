package com.ftn.pki.exceptions.issuers;

public class IssuerCertificateExpiredException extends RuntimeException {
    public IssuerCertificateExpiredException(String message) {
        super(message);
    }
}