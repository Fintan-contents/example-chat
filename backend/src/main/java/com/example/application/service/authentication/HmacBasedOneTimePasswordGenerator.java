package com.example.application.service.authentication;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;

import java.security.GeneralSecurityException;

/**
 * HOTP: An HMAC-Based One-Time Password Algorithm
 * 
 * @see <a href="https://tools.ietf.org/html/rfc4226">RFC4226</a>
 * 
 */
public final class HmacBasedOneTimePasswordGenerator {

    private static final Logger LOGGER = LoggerManager.get(HmacBasedOneTimePasswordGenerator.class);

    private final String algorithm;
    private final int digit;

    private HmacBasedOneTimePasswordGenerator(final Builder builder) {
        this.algorithm = builder.algorithm;
        this.digit = builder.digit;
    }

    public int generate(final byte[] key, final byte[] value) {

        Mac mac;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
        } catch (final GeneralSecurityException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.logError("HOTP is impossible", e);
            }
            throw new RuntimeException(e);
        }

        final byte[] hash = mac.doFinal(value);

        final int offset = hash[hash.length - 1] & 0xf;

        final int code = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16)
                | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);

        int n = 1;
        for (int i = 0; i < digit; i++) {
            n *= 10;
        }

        return code % n;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String algorithm = "HmacSHA1";
        private int digit = 6;

        private Builder() {
        }

        public Builder algorithm(final String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder digit(final int digit) {
            this.digit = digit;
            return this;
        }

        public HmacBasedOneTimePasswordGenerator build() {
            return new HmacBasedOneTimePasswordGenerator(this);
        }
    }
}