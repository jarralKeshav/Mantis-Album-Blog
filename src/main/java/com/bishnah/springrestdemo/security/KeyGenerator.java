package com.bishnah.springrestdemo.security;

import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

@Component
final class KeyGeneratorUtils{
    private KeyGeneratorUtils() {}

    static KeyPair generateKeyPair() {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return keyPair;
    }
}