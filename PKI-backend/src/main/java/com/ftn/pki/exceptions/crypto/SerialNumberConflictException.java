package com.ftn.pki.exceptions.crypto;

public class SerialNumberConflictException extends RuntimeException {
    public SerialNumberConflictException(String message) {
        super(message);
    }
}
