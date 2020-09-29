package com.example.domain.model.account;

import java.util.Objects;

public class TemporaryUserToken {

    private final String value;

    public TemporaryUserToken(String value) {
        Objects.requireNonNull(value);
        validateNotBlank(value);

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
}
