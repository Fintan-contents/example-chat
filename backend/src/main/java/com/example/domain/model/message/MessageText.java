package com.example.domain.model.message;

import java.util.Objects;

public class MessageText {

    private final String value;

    private final static int MAX_LENGTH = 500;

    public MessageText(String value) {
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
        if (value.codePointCount(0, value.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("MessageText length is too long. value=[%s]", value));
        }
    }
}
