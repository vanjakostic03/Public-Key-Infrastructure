package com.ftn.pki.exceptions.issuers;

public class IssuerCannotIssueException extends RuntimeException {
    public IssuerCannotIssueException(String message) {
        super(message);
    }
}
