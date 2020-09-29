package com.example.domain.model.channel;

import java.util.Objects;

public class ChannelName {

    private final String value;

    private final static int MAX_LENGTH = 50;

    public ChannelName(String value) {
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
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("ChannelName length is too long. value=[%s]", value));
        }
    }
}