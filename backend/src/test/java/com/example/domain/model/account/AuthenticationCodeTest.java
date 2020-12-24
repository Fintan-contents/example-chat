package com.example.domain.model.account;

import com.example.domain.model.authentication.AuthenticationCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationCodeTest {

    @Test
    void valid() {
        assertDoesNotThrow(() -> new AuthenticationCode("123456"));
    }

    @Test
    void invalidWithNull() {
        assertThrows(NullPointerException.class, () -> new AuthenticationCode(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " " })
    void invalidWith(String value) {
        assertThrows(IllegalArgumentException.class, () -> new AuthenticationCode(value));
    }
}
