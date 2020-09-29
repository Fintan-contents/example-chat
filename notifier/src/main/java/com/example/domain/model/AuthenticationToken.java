package com.example.domain.model;

public class AuthenticationToken {

    private final String value;

    public AuthenticationToken(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
