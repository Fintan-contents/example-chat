package com.example.domain.model.account;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;

public class SecretKey {

    private static final Logger LOGGER = LoggerManager.get(SecretKey.class);

    public static final SecretKey NONE = new SecretKey(null);

    private final byte[] value;

    public SecretKey(byte[] value) {
        this.value = value;
    }

    public byte[] value() {
        return value;
    }

    public static SecretKey generate() {
        try {
            SecureRandom randomizer = SecureRandom.getInstanceStrong();
            byte[] value = randomizer.generateSeed(20);
            return new SecretKey(value);

        } catch (NoSuchAlgorithmException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.logError("randomizer is not be gotten", e);
            }
            throw new RuntimeException(e);
        }
    }
}
