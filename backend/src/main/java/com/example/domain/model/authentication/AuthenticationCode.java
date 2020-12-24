package com.example.domain.model.authentication;

import java.util.Objects;

public class AuthenticationCode {

    private final String value;

    public AuthenticationCode(String value) {
        // コード値はアルゴリズム依存でシステム保持もしないため、コード値についてはチェックしない
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

    public boolean isMatch(AuthenticationCode authenticationCode) {
        return this.value.equals(authenticationCode.value);
    }
}
