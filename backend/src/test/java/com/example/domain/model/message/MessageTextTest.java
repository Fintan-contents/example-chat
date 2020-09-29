package com.example.domain.model.message;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class MessageTextTest {

    @ParameterizedTest
    @ValueSource(strings = { "テストメッセージ" })
    @MethodSource("justMaxlength")
    void validWith(String value) {
        new MessageText(value);
    }

    @Test
    void invalidWithNull() {
        assertThrows(NullPointerException.class, () -> new MessageText(null));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " " })
    @MethodSource("overMaxlength")
    void invalidWith(String value) {
        assertThrows(IllegalArgumentException.class, () -> new MessageText(value));
    }

    static Stream<String> justMaxlength() {
        String text = IntStream.range(0, 500).mapToObj(i -> "x").collect(Collectors.joining());
        return Stream.of(text);
    }

    static Stream<String> overMaxlength() {
        String s = IntStream.range(0, 501).mapToObj(i -> "x").collect(Collectors.joining());
        return Stream.of(s);
    }
}
