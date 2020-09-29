package com.example.domain.model.authentication;

import java.util.Objects;

public class AuthenticationCode {

    private final String value;

    private final static int MAX_LENGTH = 6;

    public AuthenticationCode(String value) {
        Objects.requireNonNull(value);
        validateNotBlank(value);
        validateLength(value);

        this.value = value;
    }

    public String value() {
        return value;
    }

    private static void validateNotBlank(String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("Value is blank.");
        }
    }

    public boolean isMatch(AuthenticationCode authenticationCode) {
        return this.value.equals(authenticationCode.value);
    }

    private static void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("AuthenticationCode length is too long. value=[%s]", value));
        }
    }
}
