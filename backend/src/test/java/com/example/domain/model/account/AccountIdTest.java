package com.example.domain.model.account;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class AccountIdTest {

    @ParameterizedTest
    @ValueSource(longs = { 0, 100, Long.MAX_VALUE })
    void validWith(long value) {
        new AccountId(value);
    }

    @Test
    void invalidWithNull() {
        assertThrows(NullPointerException.class, () -> new AccountId(null));
    }

    @Test
    void equals() {
        AccountId sut = new AccountId(100L);
        assertTrue(sut.equals(new AccountId(100L)));
    }

    @Test
    void notEqualsWithNull() {
        AccountId sut = new AccountId(100L);
        assertFalse(sut.equals(null));
    }

    @Test
    void notEqualsWithDifferentClass() {
        AccountId sut = new AccountId(100L);
        Long other = 100L;
        assertFalse(sut.equals(other));
    }

    @Test
    void notEquals() {
        AccountId sut = new AccountId(100L);
        assertFalse(sut.equals(new AccountId(200L)));
    }
}
