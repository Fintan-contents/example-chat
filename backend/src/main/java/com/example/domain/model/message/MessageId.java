package com.example.domain.model.message;

import java.util.Objects;

public class MessageId {

    private final Long value;

    public MessageId(Long value) {
        Objects.requireNonNull(value);

        this.value = value;
    }

    public Long value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MessageId messageId = (MessageId) o;
        return Objects.equals(value, messageId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
