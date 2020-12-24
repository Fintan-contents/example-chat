package com.example.domain.model.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MailAddressTest {

    @ParameterizedTest
    @ValueSource(strings = { "test@example.com" })
    @MethodSource("justMaxlength")
    void validWith(String value) {
        assertDoesNotThrow(() -> new MailAddress(value));
    }

    @Test
    void invalidWithNull() {
        assertThrows(NullPointerException.class, () -> new MailAddress(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "test.example.com" })
    @MethodSource("overMaxlength")
    void invalidWith(String value) {
        assertThrows(IllegalArgumentException.class, () -> new MailAddress(value));
    }

    static Stream<String> justMaxlength() {
        String text = IntStream
                .range(0, 50 - "@example.com".length())
                .mapToObj(i -> "x").collect(Collectors.joining()) + "@example.com";
        String surrogatePair = "𩸽" + text.substring(1);
        return Stream.of(text, surrogatePair);
    }

    static Stream<String> overMaxlength() {
        String text = IntStream
                .range(0, 51 - "@example.com".length())
                .mapToObj(i -> "x").collect(Collectors.joining()) + "@example.com";
        return Stream.of(text);
    }
}
