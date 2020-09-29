package com.example.domain.model.account;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TemporaryUserTokenTest {

    @Test
    void valid() {
        new TemporaryUserToken("testtoken");
    }

    @Test
    void invalidWithNull() {
        assertThrows(NullPointerException.class, () -> new TemporaryUserToken(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " " })
    void invalidWith(String value) {
        assertThrows(IllegalArgumentException.class, () -> new TemporaryUserToken(value));
    }
}
