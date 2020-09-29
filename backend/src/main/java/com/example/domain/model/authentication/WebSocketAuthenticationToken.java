package com.example.domain.model.authentication;

import java.util.Objects;

public final class WebSocketAuthenticationToken {

    private final String value;

    public WebSocketAuthenticationToken(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String value() {
        return value;
    }
}
