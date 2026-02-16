package com.ftn.pki.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class DataGenerator {
    public static void main(String[] args) throws Exception {
        // STAVI OVDE MASTER KEY KOJI SI GENERISALA
        String masterKeyBase64 = "GLSS123J8H/zWXaXiyO3DTG3QiuouPX3uXRHJA6KES0=";

        byte[] keyBytes = Base64.getDecoder().decode(masterKeyBase64);
        SecretKey masterKey = new SecretKeySpec(keyBytes, "AES");

        Utils utils = new Utils();

        // Generiši DEK za organizaciju "dfgh"
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey dek = keyGen.generateKey();

        String dekBase64 = Base64.getEncoder().encodeToString(dek.getEncoded());
        Utils.AESGcmEncrypted encrypted = utils.encrypt(masterKey, dekBase64);

        System.out.println("========================================");
        System.out.println("Kopiraj ovo u data.sql za organizaciju 'dfgh':");
        System.out.println("========================================");
        System.out.println("enc_key: '" + encrypted.getCiphertext() + "'");
        System.out.println("key_iv: '" + encrypted.getIv() + "'");
    }
}
