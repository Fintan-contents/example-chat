package com.example.domain.model.account;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class UserNameTest {

    @ParameterizedTest
    @ValueSource(strings = { "testuser" })
    @MethodSource("justMaxlength")
    void validWith(String value) {
        assertDoesNotThrow(() -> new UserName(value));
    }

    @Test
    void invalidWithNull() {
        assertThrows(NullPointerException.class, () -> new UserName(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " " })
    @MethodSource("overMaxlength")
    void invalidWith(String value) {
        assertThrows(IllegalArgumentException.class, () -> new UserName(value));
    }

    static Stream<String> justMaxlength() {
        String text = IntStream.range(0, 50).mapToObj(i -> "x").collect(Collectors.joining());
        String surrogatePair = "𩸽" + text.substring(1);
        return Stream.of(text, surrogatePair);
    }

    static Stream<String> overMaxlength() {
        String text = IntStream.range(0, 51).mapToObj(i -> "x").collect(Collectors.joining());
        return Stream.of(text);
    }
}
