package com.example.domain.model.message;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class MessageIdTest {

    @ParameterizedTest
    @ValueSource(longs = { 0, 100, Long.MAX_VALUE })
    void validWith(long value) {
        new MessageId(value);
    }

    @Test
    void invalidWithNull() {
        assertThrows(NullPointerException.class, () -> new MessageId(null));
    }

    @Test
    void equals() {
        MessageId sut = new MessageId(100L);
        assertTrue(sut.equals(new MessageId(100L)));
    }

    @Test
    void notEqualsWithNull() {
        MessageId sut = new MessageId(100L);
        assertFalse(sut.equals(null));
    }

    @Test
    void notEqualsWithDifferentClass() {
        MessageId sut = new MessageId(100L);
        Long other = 100L;
        assertFalse(sut.equals(other));
    }

    @Test
    void notEquals() {
        MessageId sut = new MessageId(100L);
        assertFalse(sut.equals(new MessageId(200L)));
    }
}
