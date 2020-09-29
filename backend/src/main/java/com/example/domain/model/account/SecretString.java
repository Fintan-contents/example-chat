package com.example.domain.model.account;

import org.apache.commons.codec.binary.Base32;

public class SecretString {

    private final String value;

    public SecretString(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static SecretString fromSecretKey(SecretKey secretKey) {
        Base32 encoder = new Base32();
        String value = encoder.encodeToString(secretKey.value());
        return new SecretString(value);
    }

    public SecretKey toSecretKey() {
        Base32 decoder = new Base32(20);
        return new SecretKey(decoder.decode(value));
    }
}
