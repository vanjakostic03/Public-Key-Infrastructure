package com.ftn.pki.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class MasterKeyGenerator {
    public static void main(String[] args) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // AES-256 (32 bytes)
        SecretKey masterKey = keyGen.generateKey();

        String base64Key = Base64.getEncoder().encodeToString(masterKey.getEncoded());

        System.out.println("========================================");
        System.out.println("MASTER KEY (32 bytes, Base64):");
        System.out.println(base64Key);
        System.out.println("========================================");
        System.out.println("Length: " + base64Key.length() + " characters");
        System.out.println("Decoded bytes: " + Base64.getDecoder().decode(base64Key).length);
        System.out.println("========================================");
        System.out.println("\nSet as environment variable:");
        System.out.println("PKI_MASTER_KEY=" + base64Key);
    }
}