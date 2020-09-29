package com.example.domain.model.account;

import java.util.Objects;

public class RawPassword {

    private final String value;

    private final static int MIN_LENGTH = 8;

    private final static int MAX_LENGTH = 50;

    public RawPassword(String value) {
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

    private static void validateLength(String value) {
        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(String.format("Password length is not long enough. value=[%s]", value));
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("Password length is too long. value=[%s]", value));
        }
    }
}
