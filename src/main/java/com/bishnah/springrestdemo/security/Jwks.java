package com.bishnah.springrestdemo.security;

import com.nimbusds.jose.jwk.RSAKey;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

public class Jwks {
    private Jwks(){}

    public static RSAKey generateRsaKey() {
        KeyPair keyPair = KeyGeneratorUtils.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }
}
